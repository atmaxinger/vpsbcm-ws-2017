import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.CompostServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ConfigServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import services.ConfigServiceTest;

public class ConfigServiceSpaceBasedTest extends ConfigServiceTest{
    MzsCore core;

    @Before
    public void setup(){

        core = DefaultMzsCore.newInstance();

        configService = new ConfigServiceImpl(core.getConfig().getSpaceUri());

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
