import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.MarketServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.PackingServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import at.ac.tuwien.complang.services.PackingServiceTest;

public class PackingServiceSpaceBasedTest extends PackingServiceTest {

    MzsCore core;

    @Before
    public void setup(){

        core = DefaultMzsCore.newInstance();

        packingService = new PackingServiceImpl(core.getConfig().getSpaceUri());

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
