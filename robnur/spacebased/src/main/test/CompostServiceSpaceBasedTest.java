import at.ac.tuwien.complang.vpbscm.robnur.shared.services.CompostServiceTest;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.CompostServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.MarketServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;

public class CompostServiceSpaceBasedTest extends CompostServiceTest {
    MzsCore core;

    @Before
    public void setup(){

        core = DefaultMzsCore.newInstance();

        compostService = new CompostServiceImpl(core.getConfig().getSpaceUri());

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
