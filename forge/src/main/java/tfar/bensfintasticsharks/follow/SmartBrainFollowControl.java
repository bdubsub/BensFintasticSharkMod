package tfar.bensfintasticsharks.follow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import tfar.bensfintasticsharks.entity.FollowMovementOwners;

import java.util.ArrayList;
import java.util.List;

/** Keeps sensors and safety tasks ticking while the follow scheduler owns navigation. */
final class SmartBrainFollowControl<E extends Mob & SmartBrainOwner<E>> {
    private final E mob;
    private final SmartBrain<E> brain;
    private final List<Entry<E>> entries = new ArrayList<>();

    SmartBrainFollowControl(E mob, SmartBrain<E> brain) {
        this.mob = mob;
        this.brain = brain;
        brain.forEachBehaviour((priority, activity, behavior, parent) -> {
            if (parent == null && (activity != Activity.CORE || behavior instanceof MoveToWalkTarget<?>)) {
                @SuppressWarnings("unchecked")
                BehaviorControl<? super E> original = (BehaviorControl<? super E>) behavior;
                entries.add(new Entry<>(priority, activity, original,
                        new SuspendedBehavior<>(original, activity == Activity.CORE)));
            }
        });
        brain.removeBehaviour(mob, (priority, activity, behavior, parent) ->
                entries.stream().anyMatch(entry -> entry.original == behavior));
        for (Entry<E> entry : entries) brain.addBehaviour(entry.priority, entry.activity, entry.wrapper);
    }

    void restore() {
        brain.removeBehaviour(mob, (priority, activity, behavior, parent) ->
                entries.stream().anyMatch(entry -> entry.wrapper == behavior));
        for (Entry<E> entry : entries) brain.addBehaviour(entry.priority, entry.activity, entry.original);
        entries.clear();
    }

    private record Entry<E extends Mob>(int priority, Activity activity,
                                       BehaviorControl<? super E> original, SuspendedBehavior<E> wrapper) {}

    private static final class SuspendedBehavior<E extends Mob> implements BehaviorControl<E> {
        private final BehaviorControl<? super E> original;
        private final boolean safetyMovement;

        private SuspendedBehavior(BehaviorControl<? super E> original, boolean safetyMovement) {
            this.original = original;
            this.safetyMovement = safetyMovement;
        }

        private boolean allowed(E mob) {
            return !FollowMovementOwners.selected(mob)
                    || safetyMovement && FollowMovementOwners.needsSafety(mob);
        }

        @Override
        public Behavior.Status getStatus() { return original.getStatus(); }

        @Override
        public boolean tryStart(ServerLevel level, E mob, long tick) {
            return allowed(mob) && original.tryStart(level, mob, tick);
        }

        @Override
        public void tickOrStop(ServerLevel level, E mob, long tick) {
            if (allowed(mob)) original.tickOrStop(level, mob, tick);
            else original.doStop(level, mob, tick);
        }

        @Override
        public void doStop(ServerLevel level, E mob, long tick) { original.doStop(level, mob, tick); }

        @Override
        public String debugString() { return original.debugString(); }
    }
}
