import org.junit.Before;
import service.CompostServiceImpl;
import service.ConfigServiceImpl;
import services.CompostServiceTest;
import services.ConfigServiceTest;

public class PostgresConfigServiceTest extends ConfigServiceTest {

    @Before
    public void setup(){

        configService = new ConfigServiceImpl();
        TestHelper.createAllTables(ConfigServiceImpl.getTables());
        init();
    }
}
