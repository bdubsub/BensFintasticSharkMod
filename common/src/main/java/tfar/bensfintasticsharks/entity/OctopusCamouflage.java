package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Bounded, server-safe substrate sampling and smooth octopus appearance blending. */
public final class OctopusCamouflage {
    private static final int SAMPLE_RADIUS = 2;
    private static final int SAMPLE_COUNT = 9;
    private static final int SAMPLE_INTERVAL = 20;
    private static final int BLEND_TICKS = 40;
    private static final int FALLBACK_COLOR = 0x6b5c4e;

    private OctopusCamouflage() {}

    public static void tick(OctopusCamouflageHost host) {
        var entity = host.camouflageEntity();
        if (entity.level().isClientSide) {
            float weight = Mth.lerp(1.0f / BLEND_TICKS, host.camouflageWeight(),
                    host.camouflageTargetWeight());
            int current = blend(host.camouflageColor(), host.camouflageTargetColor(),
                    1.0f / BLEND_TICKS);
            host.setCamouflageCurrent(current, weight);
            return;
        }
        if (entity.tickCount % SAMPLE_INTERVAL != Math.floorMod(entity.getId(), SAMPLE_INTERVAL)) return;
        BlockPos support = entity.blockPosition().below();
        if (!hasSupport(entity, support)) {
            host.setCamouflageTarget(FALLBACK_COLOR, 0.0f);
            return;
        }

        long red = 0;
        long green = 0;
        long blue = 0;
        int samples = 0;
        for (int dx = -1; dx <= 1 && samples < SAMPLE_COUNT; dx++) {
            for (int dz = -1; dz <= 1 && samples < SAMPLE_COUNT; dz++) {
                BlockPos sample = support.offset(dx, 0, dz);
                BlockState state = entity.level().getBlockState(sample);
                if (!hasSupport(entity, sample)) continue;
                MapColor color = state.getMapColor(entity.level(), sample);
                int packed = color == null ? FALLBACK_COLOR : color.col;
                red += packed >> 16 & 0xff;
                green += packed >> 8 & 0xff;
                blue += packed & 0xff;
                samples++;
            }
        }
        if (samples == 0) {
            host.setCamouflageTarget(FALLBACK_COLOR, 0.0f);
            return;
        }
        int target = (int) (red / samples) << 16
                | (int) (green / samples) << 8
                | (int) (blue / samples);
        float motion = Mth.clamp((float) entity.getDeltaMovement().length() / 0.25f, 0.0f, 1.0f);
        float concealment = Mth.clamp(1.0f - motion, 0.0f, 1.0f);
        host.setCamouflageTarget(target, concealment);
    }

    private static boolean hasSupport(net.minecraft.world.entity.Entity entity, BlockPos pos) {
        BlockState state = entity.level().getBlockState(pos);
        return !state.isAir() && state.isFaceSturdy(entity.level(), pos, net.minecraft.core.Direction.UP);
    }

    private static int blend(int current, int target, float weight) {
        float currentWeight = 1.0f - weight;
        int r = Math.round(((current >> 16 & 0xff) * currentWeight) + ((target >> 16 & 0xff) * weight));
        int g = Math.round(((current >> 8 & 0xff) * currentWeight) + ((target >> 8 & 0xff) * weight));
        int b = Math.round(((current & 0xff) * currentWeight) + ((target & 0xff) * weight));
        return r << 16 | g << 8 | b;
    }
}
