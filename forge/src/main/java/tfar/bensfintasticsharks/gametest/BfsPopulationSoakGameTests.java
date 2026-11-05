package tfar.bensfintasticsharks.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder("bfspopulation")
public final class BfsPopulationSoakGameTests {

    private BfsPopulationSoakGameTests() {
    }

    @GameTest(template = "empty", batch = "bfs_population_soak", timeoutTicks = 50_000)
    public static void naturalFishReplacementPopulationRemainsBoundedInBothModes(GameTestHelper helper) {
        BfsGameTests.runNaturalFishReplacementPopulationSoak(helper);
    }
}
