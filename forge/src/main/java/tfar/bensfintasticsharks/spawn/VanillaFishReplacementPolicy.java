package tfar.bensfintasticsharks.spawn;

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

    enum Replacement {
        ATLANTIC_COD,
        ATLANTIC_SALMON
    }
}
