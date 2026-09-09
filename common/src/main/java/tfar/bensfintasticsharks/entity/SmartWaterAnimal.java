package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public abstract class SmartWaterAnimal<T extends SmartWaterAnimal<T>> extends WaterAnimal implements SmartBrainOwner<T> {
    private int bfsBehaviorScanCooldown;
    private int bfsBehaviorActionTicks;
    private String bfsBehaviorAction = "none";
    private Entity bfsBehaviorTarget;
    private int bfsBehaviorMemoryTicks;

    protected SmartWaterAnimal(EntityType<T> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public void tick() {
        super.tick();
        SpeciesBehaviorEngine.tick(this);
    }

    int getBfsBehaviorScanCooldown() {
        return bfsBehaviorScanCooldown;
    }

    void setBfsBehaviorScanCooldown(int ticks) {
        bfsBehaviorScanCooldown = Math.max(0, ticks);
    }

    void beginBfsBehaviorAction(String action, int timeoutTicks) {
        bfsBehaviorAction = action;
        bfsBehaviorActionTicks = Math.max(1, timeoutTicks);
    }

    void rememberBfsBehaviorTarget(@Nullable Entity target, int memoryTicks) {
        bfsBehaviorTarget = target;
        bfsBehaviorMemoryTicks = target == null ? 0 : Math.max(1, memoryTicks);
    }

    boolean hasBfsBehaviorTarget() {
        return bfsBehaviorTarget != null;
    }

    boolean hasLostBfsBehaviorTarget() {
        if (bfsBehaviorTarget == null) return false;
        return bfsBehaviorTarget.isRemoved() || !bfsBehaviorTarget.isAlive()
                || bfsBehaviorTarget.level() != level();
    }

    boolean hasExpiredBfsBehaviorMemory() {
        return bfsBehaviorTarget != null && bfsBehaviorMemoryTicks <= 0;
    }

    void tickBfsBehaviorMemory() {
        if (bfsBehaviorMemoryTicks > 0) bfsBehaviorMemoryTicks--;
    }

    void clearBfsBehaviorTarget() {
        bfsBehaviorTarget = null;
        bfsBehaviorMemoryTicks = 0;
    }

    void tickBfsBehaviorAction() {
        if (bfsBehaviorActionTicks > 0) bfsBehaviorActionTicks--;
    }

    boolean hasBfsBehaviorAction() {
        return bfsBehaviorActionTicks > 0;
    }

    boolean hasExpiredBfsBehaviorAction() {
        return bfsBehaviorActionTicks == 0 && !"none".equals(bfsBehaviorAction);
    }

    void clearBfsBehaviorAction() {
        bfsBehaviorAction = "none";
        bfsBehaviorActionTicks = 0;
        clearBfsBehaviorTarget();
    }

    public String getBfsBehaviorAction() {
        return bfsBehaviorAction;
    }

    @Override
    public void remove(RemovalReason reason) {
        BrainUtils.clearMemory(getBrain(), MemoryModuleType.WALK_TARGET);
        setBfsBehaviorScanCooldown(0);
        clearBfsBehaviorAction();
        super.remove(reason);
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
