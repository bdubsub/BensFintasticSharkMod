package tfar.bensfintasticsharks.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Headline mechanic of Legacy 1.0: sharks notice splashes, attacks, and blood in
 * the water. Fire-and-forget event on the Forge event bus. The listener in
 * {@link WaterDisturbanceHandler} translates these into shark behavior changes.
 *
 * Posters live in {@link WaterDisturbanceListeners}; the consumer side lives in
 * {@link WaterDisturbanceHandler}.
 */
public class WaterDisturbanceEvent extends Event {

    public enum Type {
        /** Splash from sprint-swimming, arrow impact, fishing bobber. */
        LIGHT,
        /** Player attacks something underwater, riptide trident, big fall splash. */
        HEAVY,
        /** Living entity took damage in water. Sharks lose their minds. */
        BLOOD
    }

    private final Level level;
    private final BlockPos source;
    private final @Nullable Entity sourceEntity;
    private final Type type;

    public WaterDisturbanceEvent(Level level, BlockPos source, @Nullable Entity sourceEntity, Type type) {
        this.level = level;
        this.source = source;
        this.sourceEntity = sourceEntity;
        this.type = type;
    }

    public Level getLevel() { return level; }
    public BlockPos getSource() { return source; }
    public @Nullable Entity getSourceEntity() { return sourceEntity; }
    public Type getType() { return type; }
}
