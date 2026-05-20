package tfar.bensfintasticsharks.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Posted by sharks that broadcast to nearby kin — Oceanic Whitetip's blood-convergent
 * feeding (BloodConvergenceEvent) and Blacktip Reef's pack alert (PackAlertEvent) are
 * both implemented as variants of this single event.
 *
 * Listener: {@link SharkAlertHandler}.
 */
public class SharkAlertEvent extends Event {

    public enum Type {
        /** Whitetip on BLOOD reaction — wide-radius convergence on the bleeding entity. */
        BLOOD_CONVERGENCE,
        /** Blacktip on HOSTILE entry — same-species pack notice. */
        PACK_ALERT
    }

    private final Level level;
    private final BlockPos source;
    private final @Nullable LivingEntity target;
    private final Class<?> alertedSpecies;
    private final double radius;
    private final Type type;

    public SharkAlertEvent(Level level, BlockPos source, @Nullable LivingEntity target,
                            Class<?> alertedSpecies, double radius, Type type) {
        this.level = level;
        this.source = source;
        this.target = target;
        this.alertedSpecies = alertedSpecies;
        this.radius = radius;
        this.type = type;
    }

    public Level getLevel() { return level; }
    public BlockPos getSource() { return source; }
    public @Nullable LivingEntity getTarget() { return target; }
    public Class<?> getAlertedSpecies() { return alertedSpecies; }
    public double getRadius() { return radius; }
    public Type getType() { return type; }
}
