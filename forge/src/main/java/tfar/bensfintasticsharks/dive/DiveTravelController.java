package tfar.bensfintasticsharks.dive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.mixin.LivingEntityJumpingAccessor;

/** Applies the full suit's landlike travel while the player remains in water. */
public final class DiveTravelController {

    private static final String JUMP_LATCH = "bfs_dive_jump_latched";
    private static final float GROUND_ACCELERATION = 0.21600002F;
    private static final float AIR_ACCELERATION = 0.02F;
    private static final double GRAVITY = 0.02D;
    private static final double JUMP_IMPULSE = 0.24D;
    private static final double MAX_DOWNWARD_SPEED = -0.30D;

    private DiveTravelController() {
    }

    public static boolean apply(Player player, Vec3 input) {
        DiveSuitEligibility.Result eligibility = DiveSuitEligibility.evaluate(player);
        Vec3 beforeVelocity = player.getDeltaMovement();
        boolean supported = !(player instanceof ServerPlayer) || DiveOxygenManager.supportsSchema(player);
        if (!eligibility.eligible() || !supported) {
            clearJumpLatch(player);
            if (player instanceof ServerPlayer serverPlayer) {
                BfsDebugManager.recordDiveTravel(serverPlayer, eligibility, false,
                        supported ? "ineligible" : "schema_unsupported", input,
                        beforeVelocity, player.getDeltaMovement(), false);
            }
            return false;
        }

        boolean jumping = ((LivingEntityJumpingAccessor) player).bfs$isJumping();
        CompoundTag persistent = player.getPersistentData();
        boolean jumpEdge = jumping && !persistent.getBoolean(JUMP_LATCH) && player.onGround();
        persistent.putBoolean(JUMP_LATCH, jumping);

        float blockFriction = player.level()
                .getBlockState(player.blockPosition().below())
                .getBlock()
                .getFriction();
        float movementSpeed = player.onGround()
                ? GROUND_ACCELERATION * player.getSpeed() / (blockFriction * blockFriction * blockFriction)
                : AIR_ACCELERATION;
        player.moveRelative(movementSpeed, input);

        Vec3 velocity = player.getDeltaMovement();
        double vertical = jumpEdge
                ? JUMP_IMPULSE
                : player.isNoGravity() ? velocity.y : Math.max(velocity.y - GRAVITY, MAX_DOWNWARD_SPEED);
        player.move(MoverType.SELF, new Vec3(velocity.x, vertical, velocity.z));

        Vec3 afterMove = player.getDeltaMovement();
        if (player.onGround() && vertical <= 0.0D) {
            vertical = 0.0D;
        }
        float horizontalFriction = player.onGround() ? blockFriction * 0.91F : 0.91F;
        player.setDeltaMovement(afterMove.x * horizontalFriction, vertical, afterMove.z * horizontalFriction);
        player.calculateEntityAnimation(false);
        if (player instanceof ServerPlayer serverPlayer) {
            BfsDebugManager.recordDiveTravel(serverPlayer, eligibility, true, "applied", input,
                    beforeVelocity, player.getDeltaMovement(), jumpEdge);
        }
        return true;
    }

    private static void clearJumpLatch(Player player) {
        player.getPersistentData().remove(JUMP_LATCH);
    }
}
