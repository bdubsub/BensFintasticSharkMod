package tfar.bensfintasticsharks.disturbance;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tslat.smartbrainlib.util.BrainUtils;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.entity.BfsAquaticEntity;
import tfar.bensfintasticsharks.init.ModTags;

/**
 * 0.19 (Ben's suggestion): prey should swim away from a shark right after it's bitten.
 *
 * <p>The passive {@link BfsAquaticEntity} flee only reacts to a shark being within 12 blocks;
 * this adds the acute "just got chomped, bolt!" reaction, and — unlike the passive flee, which
 * only mod mobs have — it also kicks vanilla fish (cod/salmon/squid) that otherwise ignore a
 * shark bite. A short velocity impulse gives every victim an immediate visible dart; brain mobs
 * additionally get a sustained WALK_TARGET away, goal mobs a navigation path away.</p>
 */
public class PreyFleeHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) return;
        if (!victim.isAlive() || !victim.isInWater()) return;
        // Only shark bites trigger the flight response.
        if (!(event.getSource().getEntity() instanceof AbstractSharkEntity<?> shark)) return;
        // Sharks/orca have their own reactions (retaliate or size-flee) — don't double up.
        if (victim.getType().is(ModTags.EntityTypes.APEX_PREDATOR)) return;
        // A grabbed victim is pinned in the jaws; thrashing to flee would fight the mount and
        // look wrong, so leave the grab alone.
        if (victim.getVehicle() == shark) return;

        Vec3 delta = victim.position().subtract(shark.position());
        if (delta.horizontalDistanceSqr() < 1.0e-4) {
            delta = victim.getForward(); // degenerate overlap — just pick a heading
        }
        Vec3 away = new Vec3(delta.x, 0, delta.z).normalize();

        // Immediate dart, synced to clients via hurtMarked (forces a velocity packet).
        victim.setDeltaMovement(victim.getDeltaMovement().add(away.x * 0.45, 0.05, away.z * 0.45));
        victim.hurtMarked = true;

        // Sustained flight: 10 blocks out, fast enough to scramble but sharks still out-swim it.
        Vec3 dest = victim.position().add(away.scale(10.0));
        if (victim instanceof BfsAquaticEntity<?> brainMob) {
            BrainUtils.setMemory(brainMob.getBrain(), MemoryModuleType.WALK_TARGET,
                    new WalkTarget(dest, 1.6f, 1));
        } else if (victim instanceof PathfinderMob goalMob) {
            goalMob.getNavigation().moveTo(dest.x, dest.y, dest.z, 1.6);
        }
    }
}
