package tfar.bensfintasticsharks.gore;

import net.minecraftforge.common.ForgeConfigSpec;

/** Persistent client-local choice. This value is never sent to the server. */
public final class BfsClientGoreConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("visuals");
        ENABLED = BUILDER.comment("Show temporary blood, wound and remains effects on this client.")
                .define("gore", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private BfsClientGoreConfig() {
    }

    public static boolean enabled() {
        return ENABLED.get();
    }

    public static void setEnabled(boolean enabled) {
        ENABLED.set(enabled);
        SPEC.save();
    }
}
