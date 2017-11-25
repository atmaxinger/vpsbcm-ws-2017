import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.MarketServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import services.MarketServiceTest;

public class MarketServiceSpaceBasedTest extends MarketServiceTest {

    MzsCore core;

    @Before
    public void setup(){

        core = DefaultMzsCore.newInstance();

        marketService = new MarketServiceImpl(core.getConfig().getSpaceUri());

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        core.shutdown(true);
    }
}
