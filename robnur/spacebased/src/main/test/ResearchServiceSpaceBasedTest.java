import at.ac.tuwien.complang.vpbscm.robnur.shared.services.ResearchServiceTest;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.CompostServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ResearchServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;

public class ResearchServiceSpaceBasedTest extends ResearchServiceTest {

    MzsCore core;

    @Before
    public void setup(){

        core = DefaultMzsCore.newInstance();

        researchService = new ResearchServiceImpl(core.getConfig().getSpaceUri());

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
