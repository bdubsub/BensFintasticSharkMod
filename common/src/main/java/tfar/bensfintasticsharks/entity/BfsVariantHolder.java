package tfar.bensfintasticsharks.entity;

/**
 * Marks BFS entities that ship with selectable variants. Used by the spawn-egg cycling
 * feature so the player can pre-pick which variant the next-spawned mob will use.
 * Variants are addressed by zero-based index; out-of-range values wrap.
 */
public interface BfsVariantHolder {
    int bfsVariantCount();
    void setBfsVariantId(int id);

    /** Whether a variant belongs in the spawn egg's common-only Random choice. */
    default boolean isCommonBfsVariant(int id) {
        return id >= 0 && id < bfsVariantCount();
    }

    default int randomCommonBfsVariantId(net.minecraft.util.RandomSource random) {
        int eligibleCount = 0;
        for (int id = 0; id < bfsVariantCount(); id++) {
            if (isCommonBfsVariant(id)) eligibleCount++;
        }
        if (eligibleCount == 0) return 0;
        int selected = random.nextInt(eligibleCount);
        for (int id = 0; id < bfsVariantCount(); id++) {
            if (isCommonBfsVariant(id) && selected-- == 0) return id;
        }
        return 0;
    }
}
