package tfar.bensfintasticsharks.spawn;

import net.minecraft.world.entity.MobSpawnType;

final class VanillaFishReplacementPolicy {

    private VanillaFishReplacementPolicy() {
    }

    static Replacement replacementFor(String namespace, String path) {
        if (!"minecraft".equals(namespace)) {
            return null;
        }
        return switch (path) {
            case "cod" -> Replacement.ATLANTIC_COD;
            case "salmon" -> Replacement.ATLANTIC_SALMON;
            default -> null;
        };
    }

    static boolean replacesEntityJoinSource(MobSpawnType reason) {
        if (reason == null) {
            return false;
        }
        return switch (reason) {
            case SPAWN_EGG, COMMAND, BUCKET, DISPENSER, SPAWNER, STRUCTURE -> true;
            default -> false;
        };
    }

    enum Replacement {
        ATLANTIC_COD,
        ATLANTIC_SALMON
    }
}
