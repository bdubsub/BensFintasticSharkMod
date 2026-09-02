package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;

public abstract class SmartWaterAnimal<T extends SmartWaterAnimal<T>> extends WaterAnimal implements SmartBrainOwner<T> {
    protected SmartWaterAnimal(EntityType<T> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    protected Brain.Provider<T> brainProvider() {
        return new SmartBrainProvider<>((T)this);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain((T) this);
    }

    // Magma/soul-sand bubble columns launch water mobs to the surface (and beyond) because
    // the whirlpool/updraft velocity is applied every tick with no opt-out — Ben watched
    // sharks bounce "all the way up to the surface". BFS sea creatures are strong swimmers:
    // skip the vertical push entirely. Lives here (not BfsAquaticEntity) so the stingray
    // and harbor seal, which extend SmartWaterAnimal directly, are covered too.
    @Override
    public void onAboveBubbleCol(boolean downFlowing) {}

    @Override
    public void onInsideBubbleColumn(boolean downFlowing) {
        this.resetFallDistance(); // keep vanilla's side effect, skip the push
    }

}
