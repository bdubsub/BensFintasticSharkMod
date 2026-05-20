package tfar.bensfintasticsharks.init;

import net.minecraft.world.entity.MobCategory;

/**
 * Custom mob categories so BFS mobs don't fight vanilla's WATER_CREATURE cap (5).
 * Higher caps make the ocean feel populated. All values are tunable via spec defaults.
 *
 * Forge transforms {@link MobCategory} at load time to add the
 * {@code create(String, String, int, boolean, boolean, int)} factory used here.
 */
public class ModMobCategories {

    public static final MobCategory APEX_PREDATOR =
            MobCategory.create("bfs_apex_predator", "bensfintasticsharks:bfs_apex_predator",
                    5, false, false, 128);

    public static final MobCategory BFS_WATER_CREATURE =
            MobCategory.create("bfs_water_creature", "bensfintasticsharks:bfs_water_creature",
                    15, true, false, 128);

    public static final MobCategory BFS_WATER_AMBIENT =
            MobCategory.create("bfs_water_ambient", "bensfintasticsharks:bfs_water_ambient",
                    25, true, false, 64);

    /** No-op that forces JVM to classload this class and run static initializers. */
    public static void touch() {}
}
