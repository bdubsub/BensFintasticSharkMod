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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.entity.FollowMovementOwners;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.SmartBrainOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.EnumSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.util.Mth;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;

/** Server owned follow groups with bounded route work. */
public final class BfsFollowManager {

    public static final int MARKER_VERSION = 1;
    public static final int MAX_ROUTES_PER_TICK = 32;
    public static final int ROUTE_INTERVAL_TICKS = 10;
    public static final int BLOCKED_TICKS = 200;
    public static final double MAX_RANGE = 64.0D;
    public static final double ARRIVAL_DISTANCE = 4.0D;
    private static final double RESUME_HYSTERESIS = 0.5D;

    private static final String MARKER_VERSION_KEY = "bfs_follow_marker_version";
    private static final String ISSUE_ID_KEY = "bfs_follow_issue_id";
    private static final String OWNER_KEY = "bfs_follow_owner";
    private static final String PERSISTENT_ISSUE_KEY = "BfsFollowIssueId";

    private static final Map<UUID, Issuance> ISSUED = new HashMap<>();
    private static final Map<UUID, Group> BY_OWNER = new HashMap<>();
    private static final Map<UUID, Lease> BY_TARGET = new LinkedHashMap<>();
    private static final ArrayDeque<UUID> ROUTES = new ArrayDeque<>();
    private static final Map<UUID, Boolean> USE_RESULTS = new HashMap<>();
    private static final Map<UUID, Integer> OFFLINE_CLEANUP = new HashMap<>();
    private static long schedulerTick = Long.MIN_VALUE;
    private static int schedulerEvaluationsThisTick;
    private static int schedulerPeakEvaluations;
    private static long schedulerEvaluationsTotal;

    private BfsFollowManager() {
    }

    public static void register(IEventBus bus) {
        bus.addListener(BfsFollowManager::onServerTick);
        bus.addListener(BfsFollowManager::onServerStopping);
        bus.addListener(BfsFollowManager::onEntityLeave);
        bus.addListener(BfsFollowManager::onLivingDeath);
        bus.addListener(BfsFollowManager::onPlayerLoggedOut);
        bus.addListener(BfsFollowManager::onPlayerLoggedIn);
        bus.addListener(BfsFollowManager::onPlayerChangedDimension);
        bus.addListener(BfsFollowManager::onPlayerRespawn);
        // Entity interaction can be canceled by the target's normal mob handler or by
        // another loaded mod after the packet reaches the server. Receive the canceled
        // callback so the debug marker remains usable for every living mob family.
        bus.register(BfsFollowManager.class);
    }

    public static IssueResult issue(ServerPlayer recipient) {
        UUID ownerId = recipient.getUUID();
        recipient.stopUsingItem();
        USE_RESULTS.remove(ownerId);
        UUID issueId = UUID.randomUUID();
        ISSUED.put(ownerId, new Issuance(issueId));
        recipient.getPersistentData().putString(PERSISTENT_ISSUE_KEY, issueId.toString());
        ItemStack marker = new ItemStack(ModItems.FOLLOW_STICK);
        CompoundTag tag = marker.getOrCreateTag();
        tag.putInt(MARKER_VERSION_KEY, MARKER_VERSION);
        tag.putString(ISSUE_ID_KEY, issueId.toString());
        tag.putString(OWNER_KEY, ownerId.toString());
        equipMarker(recipient, marker);
        notifyOwner(recipient, message("issued", count(selectedCount(recipient))));
        return new IssueResult(issueId, false);
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

    public static int selectedCount(ServerPlayer owner) {
        Group group = BY_OWNER.get(owner.getUUID());
        return group == null ? 0 : group.members.size();
    }

    public static Status status(ServerPlayer owner) {
        return status(owner, 1);
    }

    public static Status status(ServerPlayer owner, int page) {
        Group group = BY_OWNER.get(owner.getUUID());
        List<Lease> members = group == null ? List.of() : new ArrayList<>(group.members.values());
        int following = 0, waiting = 0, paused = 0;
        for (Lease lease : members) {
            switch (lease.state) {
                case "following" -> following++;
                case "waiting" -> waiting++;
                default -> paused++;
            }
        }
        int pages = Math.max(1, (members.size() + 9) / 10);
        int currentPage = Math.max(1, Math.min(page, pages));
        List<MemberStatus> entries = members.stream().skip((long) (currentPage - 1) * 10).limit(10)
                .map(lease -> new MemberStatus(lease.targetId, lease.alias, lease.label, lease.state,
                        lease.reason, lease.age(owner.serverLevel().getGameTime()))).toList();
        Lease first = members.isEmpty() ? null : members.get(0);
        return new Status(hasPermission(owner), ISSUED.containsKey(owner.getUUID()), !members.isEmpty(),
                first == null ? null : first.targetType, first == null ? 0 : first.age(owner.serverLevel().getGameTime()),
                BY_TARGET.size(), members.size(), following, waiting, paused, group == null ? 0 : group.revision,
                currentPage, pages, entries);
    }

    public static boolean stop(ServerPlayer owner, String reason) {
        return stopAll(owner, reason) > 0;
    }

    public static int stopAll(ServerPlayer owner, String reason) {
        int released = releaseForOwner(owner.getUUID(), reason, owner.serverLevel(), false);
        notifyOwner(owner, message("group_released", count(released), count(selectedCount(owner))));
        return released;
    }

    public static boolean stopOne(ServerPlayer owner, Entity target) {
        target = resolveTarget(target);
        Lease lease = target == null ? null : BY_TARGET.get(target.getUUID());
        if (lease == null || !lease.ownerId.equals(owner.getUUID())) {
            notifyOwner(owner, message("not_selected"));
            return false;
        }
        release(lease, owner.serverLevel(), "command_stop", (Mob) target, true);
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        handleInteraction(event, event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        handleInteraction(event, event.getTarget());
    }

    private static void handleInteraction(PlayerInteractEvent event, Entity clicked) {
        if (!event.getItemStack().is(ModItems.FOLLOW_STICK)) return;
        if (event.getLevel().isClientSide) {
            event.getEntity().startUsingItem(event.getHand());
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer owner)) return;
        Entity target = resolveTarget(clicked);
        boolean heldRepeat = owner.isUsingItem() && owner.getUseItem().is(ModItems.FOLLOW_STICK)
                && USE_RESULTS.containsKey(owner.getUUID());
        String denied = !hasPermission(owner) ? "permission_denied"
                : !validMarkerWithoutDiagnostics(owner, event.getItemStack()) ? "invalid_stick"
                : !(target instanceof Mob mob) || !mob.isAlive() || mob.isRemoved() ? "unsupported_target"
                : owner.level() != target.level() || !owner.canReach(clicked, 0) ? "target_out_of_range" : null;
        boolean accepted = false;
        if (!heldRepeat) {
            if (denied == null) {
                accepted = toggle(owner, (Mob) target);
            } else {
                reject(owner, target, denied);
            }
            USE_RESULTS.put(owner.getUUID(), accepted);
        } else {
            accepted = denied == null && USE_RESULTS.get(owner.getUUID());
        }
        owner.startUsingItem(event.getHand());
        event.setCancellationResult(accepted ? InteractionResult.SUCCESS : InteractionResult.FAIL);
        event.setCanceled(true);
    }

    private static boolean toggle(ServerPlayer owner, Mob mob) {
        Lease existing = BY_TARGET.get(mob.getUUID());
        if (existing != null) {
            if (!existing.ownerId.equals(owner.getUUID())) {
                reject(owner, mob, "target_already_claimed");
                return false;
            }
            release(existing, owner.serverLevel(), "clicked_again", mob, true);
            return true;
        }
        Group group = BY_OWNER.computeIfAbsent(owner.getUUID(), ignored -> new Group());
        long tick = owner.serverLevel().getGameTime();
        String adapter = mob instanceof SmartBrainOwner<?> ? "smartbrain_walk_target" : "mob_navigation";
        Lease lease = new Lease(owner.getUUID(), mob, adapter, tick, group.nextAlias++);
        group.members.put(mob.getUUID(), lease);
        group.revision++;
        BY_TARGET.put(mob.getUUID(), lease);
        ROUTES.addLast(mob.getUUID());
        FollowMovementOwners.claim(mob, lease.nonce);
        lease.brainControl = claimBrain(mob);
        mob.goalSelector.addGoal(1, new FollowGoal(lease, mob, true));
        mob.targetSelector.addGoal(0, new FollowGoal(lease, mob, false));
        if (mob instanceof Slime) mob.goalSelector.addGoal(1, new SlimePauseGoal(lease));
        record(lease, owner, mob, "follow.claim", "selected");
        record(lease, owner, mob, "follow.adapter", adapter);
        notifyOwner(owner, message("selected", lease.label, count(group.members.size())));
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static SmartBrainFollowControl<?> claimBrain(Mob mob) {
        return mob instanceof SmartBrainOwner && mob.getBrain() instanceof SmartBrain brain
                ? new SmartBrainFollowControl(mob, brain) : null;
    }

    private static void reject(ServerPlayer owner, Entity target, String reason) {
        BfsDebugManager.recordFollowEvent(owner.serverLevel(), "follow.reject", owner, target,
                reason, "none", 0, 0, target == null ? 0 : owner.distanceTo(target));
        notifyOwner(owner, message(reason));
    }

    private static boolean hasPermission(ServerPlayer player) {
        return player.hasPermissions(2);
    }

    public static Component message(String key, Object... arguments) {
        return Component.translatable("bfs.follow." + key, arguments);
    }

    public static Component count(int count) {
        return message(count == 1 ? "count.one" : "count.many", count);
    }

    public static void notifyOwner(ServerPlayer owner, Component text) {
        owner.sendSystemMessage(text);
        owner.displayClientMessage(text, true);
    }

    private static boolean validIssuance(ServerPlayer owner) {
        Issuance issuance = ISSUED.get(owner.getUUID());
        return issuance != null && issuance.issueId.toString()
                .equals(owner.getPersistentData().getString(PERSISTENT_ISSUE_KEY));
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
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();
        for (Lease lease : new ArrayList<>(BY_TARGET.values())) {
            ServerPlayer owner = findPlayer(server, lease.ownerId);
            ServerLevel level = server.getLevel(lease.dimension);
            Mob mob = level != null && level.getEntity(lease.targetId) instanceof Mob found ? found : null;
            if (owner == null || mob == null || !owner.isAlive() || !mob.isAlive()) {
                release(lease, level, "lifecycle", mob, true);
                continue;
            }
            if (!hasPermission(owner) || !validIssuance(owner)) {
                release(lease, level, "permission_lost", mob, true);
                continue;
            }
            if (owner.level() != mob.level()) {
                release(lease, level, "dimension_changed", mob, true);
                continue;
            }
            double distance = owner.distanceTo(mob);
            String pause = FollowMovementOwners.needsSafety(mob) ? "safety" : !hasValidHeldMarker(owner) ? "marker_not_held"
                    : distance > MAX_RANGE ? "range_exceeded" : null;
            if (pause != null) {
                transition(lease, owner, mob, "paused", pause);
                continue;
            }
            double arrival = ARRIVAL_DISTANCE + (owner.getBbWidth() + mob.getBbWidth()) * 0.5D;
            if (distance <= arrival || lease.state.equals("waiting") && distance <= arrival + RESUME_HYSTERESIS) {
                lease.blocked = 0;
                lease.lastDistance = distance;
                transition(lease, owner, mob, "waiting", "nearby");
                continue;
            }
            if (!lease.reason.equals("no_route")) transition(lease, owner, mob, "following", "tracking");
            if (distance + 0.01D < lease.lastDistance) lease.blocked = 0;
            else lease.blocked = Math.min(BLOCKED_TICKS, lease.blocked + 1);
            lease.lastDistance = distance;
            if (lease.blocked >= BLOCKED_TICKS) transition(lease, owner, mob, "paused", "no_route");
        }
        int remaining = ROUTES.size();
        int evaluated = 0;
        long currentTick = server.overworld().getGameTime();
        if (schedulerTick != currentTick) {
            schedulerTick = currentTick;
            schedulerEvaluationsThisTick = 0;
        }
        while (remaining-- > 0 && evaluated < MAX_ROUTES_PER_TICK) {
            UUID targetId = ROUTES.removeFirst();
            Lease lease = BY_TARGET.get(targetId);
            if (lease == null) continue;
            ROUTES.addLast(targetId);
            if (!lease.state.equals("following") && !lease.reason.equals("no_route")) continue;
            ServerPlayer owner = findPlayer(server, lease.ownerId);
            ServerLevel level = server.getLevel(lease.dimension);
            Mob mob = level != null && level.getEntity(lease.targetId) instanceof Mob found ? found : null;
            if (owner == null || mob == null) continue;
            long tick = level.getGameTime();
            if (lease.lastRoute >= 0 && tick - lease.lastRoute < (lease.reason.equals("no_route") ? 20 : ROUTE_INTERVAL_TICKS)) continue;
            route(lease, owner, mob, tick);
            evaluated++;
            schedulerEvaluationsThisTick++;
            schedulerEvaluationsTotal++;
            schedulerPeakEvaluations = Math.max(schedulerPeakEvaluations, schedulerEvaluationsThisTick);
        }
        USE_RESULTS.keySet().removeIf(id -> {
            ServerPlayer owner = findPlayer(server, id);
            return owner == null || !owner.isUsingItem();
        });
    }

    private static void transition(Lease lease, ServerPlayer owner, Mob mob, String state, String reason) {
        if (lease.state.equals(state) && lease.reason.equals(reason)) return;
        lease.state = state;
        lease.reason = reason;
        Group group = BY_OWNER.get(lease.ownerId);
        if (group != null) group.revision++;
        if (!state.equals("following")) pauseMovement(lease, mob);
        record(lease, owner, mob, "follow.state", reason);
        String key = state.equals("following") ? "resumed" : state.equals("waiting") ? "waiting" : "paused";
        notifyOwner(owner, message(key, lease.label, message("reason." + reason), count(selectedCount(owner))));
    }

    private static void route(Lease lease, ServerPlayer owner, Mob mob, long tick) {
        lease.lastRoute = tick;
        if (lease.adapter.equals("smartbrain_walk_target")) {
            lease.ownedWalkTarget = new WalkTarget(owner, 1.0F, 1);
            mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, lease.ownedWalkTarget);
        }
        boolean started = mob.getNavigation().moveTo(owner, 1.0D);
        lease.ownedPath = mob.getNavigation().getPath();
        lease.ownsNavigation = true;
        if (!started) transition(lease, owner, mob, "paused", "no_route");
        else if (lease.blocked < BLOCKED_TICKS) transition(lease, owner, mob, "following", "tracking");
        record(lease, owner, mob, "follow.intent", started ? "navigation_accepted" : "navigation_rejected");
        record(lease, owner, mob, "follow.progress", lease.reason);
    }

    private static void pauseMovement(Lease lease, Mob mob) {
        if (lease.ownsNavigation && mob.getNavigation().getPath() == lease.ownedPath) mob.getNavigation().stop();
        lease.ownsNavigation = false;
        if (lease.ownedWalkTarget != null && mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null) == lease.ownedWalkTarget) {
            mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        lease.ownedWalkTarget = null;
    }

    private static void restoreController(Lease lease, Mob mob) {
        pauseMovement(lease, mob);
        if (lease.brainControl != null) lease.brainControl.restore();
        FollowMovementOwners.release(mob, lease.nonce);
        mob.goalSelector.removeAllGoals(goal -> goal instanceof FollowGoal follow && follow.lease == lease
                || goal instanceof SlimePauseGoal pause && pause.lease == lease);
        mob.targetSelector.removeAllGoals(goal -> goal instanceof FollowGoal follow && follow.lease == lease
                || goal instanceof SlimePauseGoal pause && pause.lease == lease);
        if (lease.previousWalkTarget != null && mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isEmpty()) {
            mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, lease.previousWalkTarget);
        }
    }

    private static void record(Lease lease, ServerPlayer owner, Mob mob, String event, String reason) {
        BfsDebugManager.recordFollowEvent((ServerLevel) mob.level(), event, lease.ownerId, mob, reason,
                lease.adapter, lease.age(mob.level().getGameTime()), lease.blocked,
                owner == null ? 0 : owner.distanceTo(mob),
                BY_OWNER.get(lease.ownerId).members.size(), BY_OWNER.get(lease.ownerId).revision, lease.state);
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

    private static void release(Lease lease, ServerLevel level, String reason, Mob mob, boolean feedback) {
        if (!BY_TARGET.remove(lease.targetId, lease)) return;
        ROUTES.remove(lease.targetId);
        Group group = BY_OWNER.get(lease.ownerId);
        if (group != null) {
            group.members.remove(lease.targetId);
            group.revision++;
        }
        if (mob != null) restoreController(lease, mob);
        ServerPlayer owner = level == null ? null : findPlayer(level.getServer(), lease.ownerId);
        if (mob != null) {
            record(lease, owner, mob, "follow.release", reason);
            record(lease, owner, mob, "follow.restore", "ordinary_controller_resume");
        }
        if (feedback && owner != null) notifyOwner(owner, message("released", lease.label,
                message("reason." + reason), count(selectedCount(owner))));
    }

    private static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Entity entity = event.getEntity();
        Lease lease = BY_TARGET.get(entity.getUUID());
        if (lease != null) release(lease, level, "entity_left_level", entity instanceof Mob mob ? mob : null, true);
        if (entity instanceof ServerPlayer owner) releaseForOwner(owner.getUUID(), "owner_left_level", level, true);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;
        Entity entity = event.getEntity();
        Lease lease = BY_TARGET.get(entity.getUUID());
        if (lease != null) release(lease, level, "target_died", entity instanceof Mob mob ? mob : null, true);
        if (entity instanceof ServerPlayer owner) releaseForOwner(owner.getUUID(), "owner_died", level, true);
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int released = releaseForOwner(player.getUUID(), "owner_logged_out", player.serverLevel(), false);
            if (released > 0) OFFLINE_CLEANUP.merge(player.getUUID(), released, Integer::sum);
            ISSUED.remove(player.getUUID());
            BY_OWNER.remove(player.getUUID());
            USE_RESULTS.remove(player.getUUID());
        }
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Integer released = OFFLINE_CLEANUP.remove(player.getUUID());
            if (released != null) notifyOwner(player, message("offline_cleanup", count(released)));
        }
    }

    private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) releaseForOwner(player.getUUID(), "owner_changed_dimension", player.serverLevel(), true);
    }

    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) releaseForOwner(player.getUUID(), "owner_respawned", player.serverLevel(), true);
    }

    private static int releaseForOwner(UUID ownerId, String reason, ServerLevel level, boolean feedback) {
        Group group = BY_OWNER.get(ownerId);
        if (group == null) return 0;
        int released = group.members.size();
        for (Lease lease : new ArrayList<>(group.members.values())) {
            release(lease, level, reason, findMob(level == null ? null : level.getServer(), lease.targetId), feedback);
        }
        return released;
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        for (Lease lease : new ArrayList<>(BY_TARGET.values())) {
            Mob mob = findMob(event.getServer(), lease.targetId);
            release(lease, event.getServer().getLevel(lease.dimension), "server_stopping", mob, true);
        }
        BY_OWNER.clear();
        BY_TARGET.clear();
        ROUTES.clear();
        ISSUED.clear();
        USE_RESULTS.clear();
        OFFLINE_CLEANUP.clear();
        schedulerTick = Long.MIN_VALUE;
        schedulerEvaluationsThisTick = 0;
        schedulerPeakEvaluations = 0;
        schedulerEvaluationsTotal = 0;
    }

    public record IssueResult(UUID issueId, boolean replacedLease) {}

    public record Status(boolean permitted, boolean issued, boolean following, String targetType, long age,
                         int activeLeases, int selectedCount, int followingCount, int waitingCount, int pausedCount,
                         long revision, int page, int pages, List<MemberStatus> entries) {}

    public record MemberStatus(UUID targetId, int alias, Component label, String state, String reason, long age) {}

    public record SchedulerStats(long evaluations, int peakPerTick, int currentTickEvaluations) {}

    public static SchedulerStats schedulerStats() {
        return new SchedulerStats(schedulerEvaluationsTotal, schedulerPeakEvaluations, schedulerEvaluationsThisTick);
    }

    private record Issuance(UUID issueId) {}

    private static final class Group {
        private final Map<UUID, Lease> members = new LinkedHashMap<>();
        private long revision;
        private int nextAlias = 1;
    }

    private static final class FollowGoal extends Goal {
        private final Lease lease;
        private final WeakReference<Mob> mob;
        private final boolean movement;

        private FollowGoal(Lease lease, Mob mob, boolean movement) {
            this.lease = lease;
            this.mob = new WeakReference<>(mob);
            this.movement = movement;
            setFlags(!movement ? EnumSet.of(Flag.TARGET) : mob instanceof Slime
                    ? EnumSet.of(Flag.LOOK) : EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            Mob target = mob.get();
            return target != null && BY_TARGET.get(lease.targetId) == lease
                    && !FollowMovementOwners.needsSafety(target);
        }

        @Override
        public boolean canContinueToUse() { return canUse(); }

        @Override
        public boolean requiresUpdateEveryTick() { return true; }

        @Override
        public void tick() {
            Mob target = mob.get();
            if (!movement || target == null || !lease.state.equals("following")) return;
            Path path = target.getNavigation().getPath();
            if (target.getMoveControl() instanceof Slime.SlimeMoveControl control && path != null && !path.isDone()) {
                var point = path.getNextEntityPos(target);
                float yaw = (float) (Mth.atan2(point.z - target.getZ(), point.x - target.getX()) * 180 / Math.PI) - 90;
                control.setDirection(yaw, false);
            }
        }
    }

    private static final class SlimePauseGoal extends Goal {
        private final Lease lease;

        private SlimePauseGoal(Lease lease) {
            this.lease = lease;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return BY_TARGET.get(lease.targetId) == lease && !lease.state.equals("following")
                    && !lease.reason.equals("safety");
        }
    }

    private static final class Lease {
        private final UUID nonce = UUID.randomUUID();
        private final UUID ownerId;
        private final UUID targetId;
        private final ResourceKey<Level> dimension;
        private final String targetType;
        private final Component label;
        private final int alias;
        private final String adapter;
        private final long started;
        private final WalkTarget previousWalkTarget;
        private SmartBrainFollowControl<?> brainControl;
        private WalkTarget ownedWalkTarget;
        private Path ownedPath;
        private boolean ownsNavigation;
        private long lastRoute = -1;
        private double lastDistance = Double.MAX_VALUE;
        private int blocked;
        private String state = "following";
        private String reason = "tracking";

        private Lease(UUID ownerId, Mob mob, String adapter, long started, int alias) {
            this.ownerId = ownerId;
            this.targetId = mob.getUUID();
            this.dimension = mob.level().dimension();
            this.targetType = mob.getType().builtInRegistryHolder().key().location().toString();
            this.label = message("target", mob.getName().copy(), alias);
            this.alias = alias;
            this.adapter = adapter;
            this.started = started;
            this.previousWalkTarget = adapter.equals("smartbrain_walk_target")
                    ? mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null) : null;
        }

        private long age(long tick) { return Math.max(0, tick - started); }
    }
}
