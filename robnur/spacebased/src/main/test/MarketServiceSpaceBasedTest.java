import at.ac.tuwien.complang.vpbscm.robnur.shared.services.MarketServiceTest;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.MarketServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;

public class MarketServiceSpaceBasedTest extends MarketServiceTest {

    MzsCore core;

    @Before
    public void setup(){

        core = DefaultMzsCore.newInstance();

        marketService = new MarketServiceImpl(core.getConfig().getSpaceUri());

        try {
            init();
        } catch (Exception e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @After
    public void tearDown() throws Exception {
        core.shutdown(true);
    }
}
