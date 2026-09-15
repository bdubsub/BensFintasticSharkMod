package tfar.bensfintasticsharks.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    /** Typed producer identity retained through the disturbance and diagnostic paths. */
    public enum SourceKind {
        SWIM_SPRINT("swim_sprint"),
        ATTACK("attack"),
        DAMAGE("damage"),
        BLOCK_BREAK("block_break"),
        FALL("fall"),
        PROJECTILE("projectile"),
        WATER_ENTRY("water_entry"),
        WATER_JUMP("water_jump"),
        OCCUPIED_BOAT("occupied_boat");

        private final String id;

        SourceKind(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    private final Level level;
    private final BlockPos source;
    private final @Nullable Entity sourceEntity;
    private final Type type;
    private final SourceKind sourceKind;
    private final double strength;
    private final @Nullable Entity boat;
    private final @Nullable Entity rider;
    private final Vec3 boatMovement;

    public WaterDisturbanceEvent(Level level, BlockPos source, @Nullable Entity sourceEntity, Type type) {
        this(level, source, sourceEntity, type, defaultSourceKind(type), 1.0D, null, null);
    }

    public WaterDisturbanceEvent(Level level, BlockPos source, @Nullable Entity sourceEntity, Type type,
                                 SourceKind sourceKind, double strength,
                                 @Nullable Entity boat, @Nullable Entity rider) {
        this(level, source, sourceEntity, type, sourceKind, strength, boat, rider, Vec3.ZERO);
    }

    public WaterDisturbanceEvent(Level level, BlockPos source, @Nullable Entity sourceEntity, Type type,
                                 SourceKind sourceKind, double strength,
                                 @Nullable Entity boat, @Nullable Entity rider, Vec3 boatMovement) {
        this.level = level;
        this.source = source;
        this.sourceEntity = sourceEntity;
        this.type = type;
        this.sourceKind = sourceKind;
        this.strength = Math.max(0.0D, Math.min(1.0D, strength));
        this.boat = boat;
        this.rider = rider;
        this.boatMovement = boatMovement == null || !finite(boatMovement) ? Vec3.ZERO : boatMovement;
    }

    public Level getLevel() { return level; }
    public BlockPos getSource() { return source; }
    public @Nullable Entity getSourceEntity() { return sourceEntity; }
    public Type getType() { return type; }
    public SourceKind getSourceKind() { return sourceKind; }
    public double getStrength() { return strength; }
    public @Nullable Entity getBoat() { return boat; }
    public @Nullable Entity getRider() { return rider; }
    public Vec3 getBoatMovement() { return boatMovement; }

    private static boolean finite(Vec3 value) {
        return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z);
    }

    private static SourceKind defaultSourceKind(Type type) {
        return switch (type) {
            case LIGHT -> SourceKind.SWIM_SPRINT;
            case HEAVY -> SourceKind.ATTACK;
            case BLOOD -> SourceKind.DAMAGE;
        };
    }
}
