package tfar.bensfintasticsharks.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityJumpingAccessor {

    @Accessor("jumping")
    boolean bfs$isJumping();

    @Accessor("jumping")
    void bfs$setJumping(boolean jumping);
}
