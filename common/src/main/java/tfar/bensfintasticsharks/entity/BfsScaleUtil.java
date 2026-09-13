package tfar.bensfintasticsharks.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;

/** Shared scale resolution for entities outside the common aquatic base class. */
public final class BfsScaleUtil {

    private BfsScaleUtil() {}

    public static float roll(Entity entity, RandomSource random, float fallbackMin, float fallbackMax) {
        float min = (float) SpeciesSettingsService.valueFor(entity,
                SpeciesSettingsService.Field.SCALE_MIN, fallbackMin);
        float max = (float) SpeciesSettingsService.valueFor(entity,
                SpeciesSettingsService.Field.SCALE_MAX, fallbackMax);
        min = Math.max(0.25F, Math.min(2.0F, min));
        max = Math.max(min, Math.min(2.0F, max));
        return min + random.nextFloat() * (max - min);
    }

    public static EntityDimensions scale(EntityDimensions dimensions, float scale) {
        return Math.abs(scale - 1.0F) < 0.001F ? dimensions : dimensions.scale(scale);
    }
}
