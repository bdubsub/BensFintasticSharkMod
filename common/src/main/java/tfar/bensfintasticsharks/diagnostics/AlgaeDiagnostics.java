package tfar.bensfintasticsharks.diagnostics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/** Loader neutral event bridge used by the Forge debug capture for algae state changes. */
public final class AlgaeDiagnostics {

    private static volatile Consumer<Event> listener = event -> {
    };

    private AlgaeDiagnostics() {
    }

    public static void install(Consumer<Event> nextListener) {
        listener = nextListener == null ? event -> {
        } : nextListener;
    }

    public static void emit(LevelAccessor level, BlockPos pos, String event, String reason,
                            @Nullable Entity actor, @Nullable BlockState before,
                            @Nullable BlockState after, Map<String, Object> details) {
        try {
            listener.accept(new Event(level, pos.immutable(), event, reason, actor, before, after,
                    details == null || details.isEmpty() ? Map.of() : Map.copyOf(details)));
        } catch (RuntimeException ignored) {
            // Diagnostics must never change block behavior or break a world tick.
        }
    }

    public record Event(LevelAccessor level, BlockPos pos, String event, String reason,
                        @Nullable Entity actor, @Nullable BlockState before,
                        @Nullable BlockState after, Map<String, Object> details) {
    }
}
