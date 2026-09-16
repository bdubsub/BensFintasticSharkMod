package tfar.bensfintasticsharks.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.bensfintasticsharks.dive.DiveTravelController;

@Mixin(LivingEntity.class)
public abstract class DiveTravelMixin {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void bfs$travel(Vec3 travelVector, CallbackInfo callbackInfo) {
        if ((Object) this instanceof Player player && DiveTravelController.apply(player, travelVector)) {
            callbackInfo.cancel();
        }
    }
}
