package verification;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.bensfintasticsharks.gametest.BfsFollowThirdPartyGameTests;

/** Runs the existing compatibility fixture against installed Forge using its runtime names. */
@Mod("bfs_packaged_verification")
public final class PackagedCompatibilityProbe {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GameTestTicker ticker = new GameTestTicker();
    private MultipleTestTracker tracker;
    private int ticks;
    private boolean finished;

    public PackagedCompatibilityProbe() {
        MinecraftForge.EVENT_BUS.addListener(this::started);
        MinecraftForge.EVENT_BUS.addListener(this::tick);
    }

    private void started(ServerStartedEvent event) {
        GameTestRegistry.m_177501_(BfsFollowThirdPartyGameTests.class);
        var tests = GameTestRegistry.m_127658_();
        if (tests.size() != 1) {
            throw new IllegalStateException("Expected exactly one packaged compatibility fixture.");
        }
        tracker = new MultipleTestTracker(GameTestRunner.m_127752_(tests,
                new BlockPos(0, -60, 0), Rotation.NONE, event.getServer().m_129783_(), ticker, 1));
        tracker.m_127807_(test -> LOGGER.error("Packaged fixture failed. {}",
                test.m_127633_(), test.m_127642_()));
        LOGGER.info("Packaged compatibility fixture started. Required tests {}", tests.size());
    }

    private void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || tracker == null || finished) return;
        ticker.m_127790_();
        if (tracker.m_127821_() || ++ticks >= 400) {
            finished = true;
            LOGGER.info("Packaged compatibility result. Complete {}. Failed required {}. Done {}. Total {}.",
                    tracker.m_127821_(), tracker.m_127803_(), tracker.m_127817_(), tracker.m_127820_());
            event.getServer().m_7570_(false);
        }
    }
}
