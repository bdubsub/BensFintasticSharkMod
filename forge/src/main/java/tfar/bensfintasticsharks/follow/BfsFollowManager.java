package tfar.bensfintasticsharks.follow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.EventPriority;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.init.ModItems;
import net.tslat.smartbrainlib.api.SmartBrainOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Server owned, bounded leases used by the follow debug command. */
public final class BfsFollowManager {

    public static final int MARKER_VERSION = 1;
    public static final int MAX_LEASES = 32;
    public static final int ROUTE_INTERVAL_TICKS = 10;
    public static final int LEASE_DURATION_TICKS = 2_400;
    public static final int BLOCKED_TICKS = 200;
    public static final double MAX_RANGE = 64.0D;
    public static final double ARRIVAL_DISTANCE = 4.0D;
    private static final double ARRIVAL_RESELECT_DISTANCE = ARRIVAL_DISTANCE + 0.5D;

    private static final String MARKER_VERSION_KEY = "bfs_follow_marker_version";
    private static final String ISSUE_ID_KEY = "bfs_follow_issue_id";
    private static final String OWNER_KEY = "bfs_follow_owner";
    private static final String PERSISTENT_ISSUE_KEY = "BfsFollowIssueId";

    private static final Map<UUID, Issuance> ISSUED = new HashMap<>();
    private static final Map<UUID, Lease> BY_OWNER = new HashMap<>();
    private static final Map<UUID, Lease> BY_TARGET = new HashMap<>();
    private static final Set<FollowKey> ARRIVAL_LATCHES = new HashSet<>();
    private static final Set<String> INTERACTIONS = new HashSet<>();
    private static final Map<UUID, Long> REJECTION_FEEDBACK = new HashMap<>();
    private static long interactionTick = Long.MIN_VALUE;

    private BfsFollowManager() {
    }

    public static void register(IEventBus bus) {
        bus.addListener(BfsFollowManager::onServerTick);
        bus.addListener(BfsFollowManager::onServerStopping);
        bus.addListener(BfsFollowManager::onEntityLeave);
        bus.addListener(BfsFollowManager::onLivingDeath);
        bus.addListener(BfsFollowManager::onPlayerLoggedOut);
        bus.addListener(BfsFollowManager::onPlayerChangedDimension);
        bus.addListener(BfsFollowManager::onPlayerRespawn);
        // Entity interaction can be canceled by the target's normal mob handler or by
        // another loaded mod after the packet reaches the server. Receive the canceled
        // callback so the debug marker remains usable for every living mob family.
        bus.addListener(EventPriority.LOWEST, true, BfsFollowManager::onEntityInteract);
        bus.addListener(EventPriority.LOWEST, true, BfsFollowManager::onEntityInteractSpecific);
    }

    public static IssueResult issue(ServerPlayer recipient) {
        UUID ownerId = recipient.getUUID();
        Lease existing = BY_OWNER.get(ownerId);
        boolean replacedLease = existing != null;
        if (existing != null) {
            release(existing, recipient.serverLevel(), "marker_reissued",
                    findMob(recipient.serverLevel().getServer(), existing.targetId));
        }
        ARRIVAL_LATCHES.removeIf(key -> key.ownerId().equals(ownerId));
        UUID issueId = UUID.randomUUID();
        ISSUED.put(ownerId, new Issuance(issueId));
        recipient.getPersistentData().putString(PERSISTENT_ISSUE_KEY, issueId.toString());
        ItemStack marker = new ItemStack(ModItems.FOLLOW_STICK);
        CompoundTag tag = marker.getOrCreateTag();
        tag.putInt(MARKER_VERSION_KEY, MARKER_VERSION);
        tag.putString(ISSUE_ID_KEY, issueId.toString());
        tag.putString(OWNER_KEY, ownerId.toString());
        equipMarker(recipient, marker);
        return new IssueResult(issueId, replacedLease);
    }

    private static void equipMarker(ServerPlayer recipient, ItemStack marker) {
        ItemStack mainHand = recipient.getMainHandItem();
        if (mainHand.isEmpty() || mainHand.is(ModItems.FOLLOW_STICK)) {
            recipient.setItemInHand(InteractionHand.MAIN_HAND, marker);
            return;
        }
        for (int slot = 0; slot < 9; slot++) {
            if (recipient.getInventory().getItem(slot).isEmpty()) {
                recipient.getInventory().setItem(slot, marker);
                recipient.getInventory().selected = slot;
                recipient.getInventory().setChanged();
                recipient.containerMenu.broadcastChanges();
                recipient.connection.send(new ClientboundSetCarriedItemPacket(slot));
                return;
            }
        }
        if (recipient.getOffhandItem().is(ModItems.FOLLOW_STICK)) {
            recipient.setItemInHand(InteractionHand.OFF_HAND, marker);
            return;
        }
        ItemStack displaced = mainHand.copy();
        recipient.setItemInHand(InteractionHand.MAIN_HAND, marker);
        if (!recipient.getInventory().add(displaced)) {
            recipient.drop(displaced, false);
        }
    }

    public static Status status(ServerPlayer owner) {
        Lease lease = BY_OWNER.get(owner.getUUID());
        Issuance issuance = ISSUED.get(owner.getUUID());
        return new Status(issuance != null, lease != null, lease == null ? null : lease.targetType,
                lease == null ? 0 : lease.age(owner.serverLevel().getGameTime()), BY_OWNER.size());
    }

    public static boolean stop(ServerPlayer owner, String reason) {
        Lease lease = BY_OWNER.get(owner.getUUID());
        if (lease == null) {
            return false;
        }
        release(lease, owner.serverLevel(), reason, findMob(owner.serverLevel().getServer(), lease.targetId));
        return true;
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ServerPlayer owner)) {
            return;
        }
        Entity target = resolveTarget(event.getTarget());
        ClaimResult result = claim(owner, target, event.getItemStack());
        if (!result.accepted()) {
            sendRejectionFeedback(owner, result.reason());
        }
        if (result.accepted()) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ServerPlayer owner)) {
            return;
        }
        Entity target = resolveTarget(event.getTarget());
        ClaimResult result = claim(owner, target, event.getItemStack());
        if (!result.accepted()) {
            sendRejectionFeedback(owner, result.reason());
        }
        if (result.accepted()) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static ClaimResult claim(ServerPlayer owner, Entity target, ItemStack marker) {
        if (!validMarker(owner, marker)) {
            return ClaimResult.rejected("marker_unavailable");
        }
        if (!(target instanceof Mob mob) || target instanceof ServerPlayer || !mob.isAlive() || mob.isRemoved()) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                    "unsupported_target", "none", 0, 0, 0.0D);
            return ClaimResult.rejected("unsupported_target");
        }
        if (target.level() != owner.level() || owner.distanceToSqr(target) > MAX_RANGE * MAX_RANGE) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                    "target_out_of_range", "navigation", 0, 0, owner.distanceTo(target));
            return ClaimResult.rejected("target_out_of_range");
        }
        long tick = owner.serverLevel().getGameTime();
        FollowKey followKey = new FollowKey(owner.getUUID(), target.getUUID());
        String interaction = owner.getUUID() + ":" + target.getUUID() + ":" + tick;
        if (tick != interactionTick) {
            interactionTick = tick;
            INTERACTIONS.clear();
        }
        if (!INTERACTIONS.add(interaction)) {
            return ClaimResult.rejected("duplicate_interaction");
        }
        Lease ownerLease = BY_OWNER.get(owner.getUUID());
        if (ownerLease != null) {
            if (ownerLease.targetId.equals(target.getUUID())) {
                // Repeated right click events are emitted while the marker is held. Keep
                // the existing lease instead of treating the repeat as a toggle.
                BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reselect", owner, mob,
                        "already_claimed", ownerLease.adapter, ownerLease.age(tick), ownerLease.blocked,
                        owner.distanceTo(target));
                return ClaimResult.success();
            }
            Lease targetLease = BY_TARGET.get(target.getUUID());
            if (targetLease != null) {
                BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                        "target_already_claimed", targetLease.adapter, targetLease.age(tick), targetLease.blocked, owner.distanceTo(target));
                return ClaimResult.rejected("target_already_claimed");
            }
            release(ownerLease, owner.serverLevel(), "reselected", findMob(owner.serverLevel().getServer(), ownerLease.targetId));
        }
        if (ARRIVAL_LATCHES.contains(followKey)) {
            if (owner.distanceTo(target) <= ARRIVAL_RESELECT_DISTANCE) {
                BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                        "arrival_latched", "none", 0, 0, owner.distanceTo(target));
                return ClaimResult.rejected("arrival_cooldown");
            }
            ARRIVAL_LATCHES.remove(followKey);
        }
        Lease targetLease = BY_TARGET.get(target.getUUID());
        if (targetLease != null) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                    "target_already_claimed", targetLease.adapter, targetLease.age(tick), targetLease.blocked, owner.distanceTo(target));
            return ClaimResult.rejected("target_already_claimed");
        }
        if (BY_OWNER.size() >= MAX_LEASES) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                    "lease_limit", "navigation", 0, 0, owner.distanceTo(target));
            return ClaimResult.rejected("lease_limit");
        }
        String adapter = mob instanceof SmartBrainOwner<?> ? "smartbrain_walk_target" : "mob_navigation";
        Lease lease = new Lease(owner.getUUID(), target.getUUID(),
                target.getType().builtInRegistryHolder().key().location().toString(), adapter, tick);
        BY_OWNER.put(lease.ownerId, lease);
        BY_TARGET.put(lease.targetId, lease);
        BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.claim", owner, target,
                "claimed", lease.adapter, 0, 0, owner.distanceTo(target));
        BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.adapter", owner, target,
                lease.adapter, lease.adapter, 0, 0, owner.distanceTo(target));
        route(lease, owner, mob, tick);
        return ClaimResult.success();
    }

    private static boolean validMarker(ServerPlayer owner, ItemStack marker) {
        if (!hasPermission(owner)) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, null,
                    "permission_denied", "none", 0, 0, 0.0D);
            return false;
        }
        if (!marker.is(ModItems.FOLLOW_STICK) || !marker.hasTag()) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, null,
                    "invalid_stick", "none", 0, 0, 0.0D);
            return false;
        }
        CompoundTag tag = marker.getTag();
        if (tag.getInt(MARKER_VERSION_KEY) != MARKER_VERSION
                || !owner.getUUID().toString().equals(tag.getString(OWNER_KEY))) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, null,
                    "invalid_stick", "none", 0, 0, 0.0D);
            return false;
        }
        try {
            UUID issueId = UUID.fromString(tag.getString(ISSUE_ID_KEY));
            Issuance issuance = ISSUED.get(owner.getUUID());
            boolean valid = issuance != null && issuance.issueId.equals(issueId)
                    && issueId.toString().equals(owner.getPersistentData().getString(PERSISTENT_ISSUE_KEY));
            if (!valid) {
                BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, null,
                        "stale_marker", "none", 0, 0, 0.0D);
            }
            return valid;
        } catch (IllegalArgumentException ignored) {
            BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, null,
                    "invalid_stick", "none", 0, 0, 0.0D);
            return false;
        }
    }

    private static boolean hasPermission(ServerPlayer player) {
        return player.serverLevel().getServer().getPlayerList().isOp(player.getGameProfile());
    }

    private static void sendRejectionFeedback(ServerPlayer owner, String reason) {
        long tick = owner.serverLevel().getGameTime();
        Long previousTick = REJECTION_FEEDBACK.put(owner.getUUID(), tick);
        if (previousTick != null && previousTick == tick) {
            return;
        }
        String message = switch (reason) {
            case "permission_denied" -> "Follow debug stick requires operator permission.";
            case "invalid_stick", "stale_marker" -> "This follow debug stick is invalid. Issue a fresh one with /bfs debug followme.";
            case "unsupported_target" -> "That entity cannot be followed by the debug stick.";
            case "target_out_of_range" -> "The target is too far away for the follow debug stick.";
            case "target_already_claimed" -> "That entity is already claimed by another follow lease.";
            case "lease_limit" -> "The follow debug lease limit is reached.";
            case "arrival_cooldown" -> "That entity just arrived. Move farther away before selecting it again.";
            default -> "The follow debug stick could not claim that entity.";
        };
        owner.sendSystemMessage(Component.literal(message));
    }

    private static boolean hasValidHeldMarker(ServerPlayer owner) {
        return validMarkerWithoutDiagnostics(owner, owner.getMainHandItem())
                || validMarkerWithoutDiagnostics(owner, owner.getOffhandItem());
    }

    private static boolean validMarkerWithoutDiagnostics(ServerPlayer owner, ItemStack marker) {
        if (!marker.is(ModItems.FOLLOW_STICK) || !marker.hasTag()) {
            return false;
        }
        CompoundTag tag = marker.getTag();
        if (tag.getInt(MARKER_VERSION_KEY) != MARKER_VERSION
                || !owner.getUUID().toString().equals(tag.getString(OWNER_KEY))) {
            return false;
        }
        try {
            UUID issueId = UUID.fromString(tag.getString(ISSUE_ID_KEY));
            Issuance issuance = ISSUED.get(owner.getUUID());
            return issuance != null && issuance.issueId.equals(issueId)
                    && issueId.toString().equals(owner.getPersistentData().getString(PERSISTENT_ISSUE_KEY));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();
        for (Lease lease : new ArrayList<>(BY_OWNER.values())) {
            ServerPlayer owner = findPlayer(server, lease.ownerId);
            Mob mob = findMob(server, lease.targetId);
            if (owner == null || mob == null || !owner.isAlive() || !mob.isAlive()) {
                release(lease, owner == null ? null : owner.serverLevel(), "lifecycle", mob);
                continue;
            }
            if (!hasPermission(owner)) {
                release(lease, owner.serverLevel(), "permission_lost", mob);
                continue;
            }
            if (!hasValidHeldMarker(owner)) {
                release(lease, owner.serverLevel(), "marker_lost", mob);
                continue;
            }
            if (owner.level() != mob.level()) {
                release(lease, owner.serverLevel(), "dimension_changed", mob);
                continue;
            }
            ServerLevel level = owner.serverLevel();
            long tick = level.getGameTime();
            double distance = owner.distanceTo(mob);
            if (distance > MAX_RANGE) {
                release(lease, level, "range_exceeded", mob);
                continue;
            }
            if (tick - lease.started >= LEASE_DURATION_TICKS) {
                release(lease, level, "timeout", mob);
                continue;
            }
            if (distance <= ARRIVAL_DISTANCE) {
                release(lease, level, "arrived", mob);
                continue;
            }
            if (distance + 0.01D < lease.lastDistance) {
                lease.blocked = 0;
            } else {
                lease.blocked++;
            }
            lease.lastDistance = distance;
            if (lease.blocked == 1 || lease.blocked == BLOCKED_TICKS) {
                BfsDebugManager.recordFollowEvent(level, "follow.block", owner, mob,
                        "no_route_progress", lease.adapter, lease.age(tick), lease.blocked, distance);
            }
            if (lease.blocked >= BLOCKED_TICKS) {
                release(lease, level, "blocked", mob);
                continue;
            }
            if (tick - lease.lastRoute >= ROUTE_INTERVAL_TICKS) {
                route(lease, owner, mob, tick);
            }
            BfsDebugManager.recordFollowEvent(level, "follow.progress", owner, mob,
                    "tracking", lease.adapter, lease.age(tick), lease.blocked, distance);
        }
    }

    private static void route(Lease lease, ServerPlayer owner, Mob mob, long tick) {
        lease.lastRoute = tick;
        boolean started;
        if (lease.adapter.equals("smartbrain_walk_target")) {
            if (lease.previousWalkTarget == null) {
                lease.previousWalkTarget = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
            }
            mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(owner, 1.0F, 1));
            // SmartBrainLib normally turns WALK_TARGET into a navigation request, but an
            // active idle activity can replace that request during the same tick. Submit the
            // navigation request directly as well so custom aquatic move controls receive the
            // owner destination immediately and keep moving instead of only looking at it.
            started = mob.getNavigation().moveTo(owner, 1.0D);
        } else {
            started = mob.getNavigation().moveTo(owner, 1.0D);
        }
        BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.intent", owner, mob,
                started ? "navigation_accepted" : "navigation_rejected", lease.adapter, lease.age(tick), lease.blocked,
                owner.distanceTo(mob));
    }

    private static void restoreController(Lease lease, Mob mob) {
        if (lease.adapter.equals("smartbrain_walk_target")) {
            Brain<?> brain = mob.getBrain();
            WalkTarget current = brain.getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
            if (ownsWalkTarget(current, lease.ownerId)) {
                if (lease.previousWalkTarget == null) {
                    brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                } else {
                    brain.setMemory(MemoryModuleType.WALK_TARGET, lease.previousWalkTarget);
                }
            }
        }
        mob.getNavigation().stop();
    }

    private static boolean ownsWalkTarget(WalkTarget walkTarget, UUID ownerId) {
        return walkTarget != null && walkTarget.getTarget() instanceof EntityTracker tracker
                && tracker.getEntity().getUUID().equals(ownerId);
    }

    private static Mob findMob(MinecraftServer server, UUID targetId) {
        if (server == null) {
            return null;
        }
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(targetId);
            if (entity instanceof Mob mob) {
                return mob;
            }
        }
        return null;
    }

    private static ServerPlayer findPlayer(MinecraftServer server, UUID ownerId) {
        if (server == null) {
            return null;
        }
        ServerPlayer connected = server.getPlayerList().getPlayer(ownerId);
        if (connected != null) {
            return connected;
        }
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(ownerId);
            if (entity instanceof ServerPlayer player) {
                return player;
            }
        }
        return null;
    }

    private static Entity resolveTarget(Entity entity) {
        return entity instanceof EnderDragonPart part ? part.getParent() : entity;
    }

    private static void release(Lease lease, ServerLevel level, String reason, Mob mob) {
        if (BY_OWNER.remove(lease.ownerId, lease)) {
            BY_TARGET.remove(lease.targetId, lease);
            FollowKey followKey = new FollowKey(lease.ownerId, lease.targetId);
            if ("arrived".equals(reason)) {
                ARRIVAL_LATCHES.add(followKey);
            } else {
                ARRIVAL_LATCHES.remove(followKey);
            }
            if (mob != null) {
                restoreController(lease, mob);
            }
            if (level != null) {
                ServerPlayer owner = findPlayer(level.getServer(), lease.ownerId);
                long age = lease.age(level.getGameTime());
                double distance = owner == null || mob == null ? 0.0D : owner.distanceTo(mob);
                BfsDebugManager.recordFollowEvent(level, "follow.release", owner, mob, reason,
                        lease.adapter, age, lease.blocked, distance);
                BfsDebugManager.recordFollowEvent(level, "follow.restore", owner, mob,
                        "ordinary_controller_resume", lease.adapter, age, lease.blocked, distance);
            }
        }
    }

    private static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        releaseForTarget(event.getEntity().getUUID(), "entity_left_level", event.getLevel() instanceof ServerLevel sl ? sl : null);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        releaseForTarget(entity.getUUID(), "target_died", entity.level() instanceof ServerLevel sl ? sl : null);
        if (entity instanceof ServerPlayer player) {
            releaseForOwner(player.getUUID(), "owner_died", player.serverLevel());
        }
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            releaseForOwner(player.getUUID(), "owner_logged_out", player.serverLevel());
            ISSUED.remove(player.getUUID());
            REJECTION_FEEDBACK.remove(player.getUUID());
        }
    }

    private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            releaseForOwner(player.getUUID(), "owner_changed_dimension", player.serverLevel());
        }
    }

    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            releaseForOwner(player.getUUID(), "owner_respawned", player.serverLevel());
        }
    }

    private static void releaseForOwner(UUID ownerId, String reason, ServerLevel level) {
        Lease lease = BY_OWNER.get(ownerId);
        if (lease != null) {
            release(lease, level, reason, findMob(level == null ? null : level.getServer(), lease.targetId));
        }
    }

    private static void releaseForTarget(UUID targetId, String reason, ServerLevel level) {
        Lease lease = BY_TARGET.get(targetId);
        if (lease != null) {
            release(lease, level, reason, findMob(level == null ? null : level.getServer(), targetId));
        }
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        for (Lease lease : new ArrayList<>(BY_OWNER.values())) {
            Mob mob = findMob(event.getServer(), lease.targetId);
            release(lease, mob == null || !(mob.level() instanceof ServerLevel sl) ? null : sl, "server_stopping", mob);
        }
        BY_OWNER.clear();
        BY_TARGET.clear();
        ISSUED.clear();
        INTERACTIONS.clear();
        REJECTION_FEEDBACK.clear();
        ARRIVAL_LATCHES.clear();
    }

    public record IssueResult(UUID issueId, boolean replacedLease) {
    }

    public record Status(boolean issued, boolean following, String targetType, long age, int activeLeases) {
    }

    public record ClaimResult(boolean accepted, String reason) {
        private static ClaimResult success() {
            return new ClaimResult(true, "accepted");
        }

        private static ClaimResult rejected(String reason) {
            return new ClaimResult(false, reason);
        }
    }

    private record Issuance(UUID issueId) {
    }

    private record FollowKey(UUID ownerId, UUID targetId) {
    }

    private static final class Lease {
        private final UUID ownerId;
        private final UUID targetId;
        private final String targetType;
        private final String adapter;
        private final long started;
        private WalkTarget previousWalkTarget;
        private long lastRoute = Long.MIN_VALUE;
        private double lastDistance = Double.MAX_VALUE;
        private int blocked;

        private Lease(UUID ownerId, UUID targetId, String targetType, String adapter, long started) {
            this.ownerId = ownerId;
            this.targetId = targetId;
            this.targetType = targetType;
            this.adapter = adapter;
            this.started = started;
        }

        private long age(long tick) {
            return Math.max(0, tick - started);
        }
    }
}
