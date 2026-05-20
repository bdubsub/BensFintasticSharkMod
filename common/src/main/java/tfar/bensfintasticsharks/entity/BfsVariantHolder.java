package tfar.bensfintasticsharks.entity;

/**
 * Marks BFS entities that ship with selectable variants. Used by the spawn-egg cycling
 * feature so the player can pre-pick which variant the next-spawned mob will use.
 * Variants are addressed by zero-based index; out-of-range values wrap.
 */
public interface BfsVariantHolder {
    int bfsVariantCount();
    void setBfsVariantId(int id);
}
