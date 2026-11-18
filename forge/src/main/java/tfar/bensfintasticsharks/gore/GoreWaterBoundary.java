package tfar.bensfintasticsharks.gore;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;

public final class GoreWaterBoundary {
    private GoreWaterBoundary() {
    }

    /** Returns NaN when the sprite cannot fit below the water surface in this column. */
    public static double submergedCenter(BlockGetter level, double x, double y, double z, float halfSize) {
        BlockPos.MutableBlockPos pos = BlockPos.containing(x, y, z).mutable();
        int reach = (int) Math.ceil(halfSize * Math.sqrt(2.0D)) + 2;
        int bottom = pos.getY() - reach;
        while (!level.getFluidState(pos).is(FluidTags.WATER)) {
            if (pos.getY() <= bottom) return Double.NaN;
            pos.move(0, -1, 0);
        }
        int top = pos.getY() + reach;
        while (pos.getY() < top && level.getFluidState(pos.above()).is(FluidTags.WATER)) pos.move(0, 1, 0);

        double surface = pos.getY() + level.getFluidState(pos).getHeight(level, pos);
        double center = centerBelowSurface(y, surface, halfSize);
        pos.set(x, center, z);
        var fluid = level.getFluidState(pos);
        return fluid.is(FluidTags.WATER) && center < pos.getY() + fluid.getHeight(level, pos)
                ? center : Double.NaN;
    }

    public static double centerBelowSurface(double centerY, double surfaceY, float halfSize) {
        return Math.min(centerY, surfaceY - halfSize * Math.sqrt(2.0D) - 0.01D);
    }
}
