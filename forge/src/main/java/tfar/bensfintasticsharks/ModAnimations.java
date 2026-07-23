package tfar.bensfintasticsharks;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

public class ModAnimations {
    public static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("move.fast_swim");

    /**
     * 0.19 — couple swim-clip playback to actual horizontal speed so the tail-beat matches
     * how fast the shark is really moving (Ben: "the swimming seems a bit stiff"). At rest it
     * returns 1.0 (authored speed), ramping to 1.5× at a full chase. Because it keys off
     * velocity, it self-gates on the non-swim states — a braking shark about to bite, a dying
     * shark, or a beached one all have ~zero horizontal speed, so their clips stay at 1.0 and
     * bite timing (biteImpactDelayTicks) is left intact.
     */
    public static double swimClipSpeed(LivingEntity e) {
        Vec3 v = e.getDeltaMovement();
        double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
        return Mth.clamp(1.0 + horiz * 2.0, 1.0, 1.5);
    }
    public static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("misc.death");
    public static final RawAnimation THRASH = RawAnimation.begin().thenLoop("attack.thrash");
    public static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("misc.beached");
    public static final RawAnimation BEACHED2 = RawAnimation.begin().thenLoop("misc.beached2");

    public static final RawAnimation BASK = RawAnimation.begin().thenLoop("misc.bask");
    public static final RawAnimation BASK2 = RawAnimation.begin().thenLoop("bask2");
    public static final RawAnimation BASK3 = RawAnimation.begin().thenLoop("bask3");
    public static final RawAnimation BASK4 = RawAnimation.begin().thenLoop("bask4");
}
