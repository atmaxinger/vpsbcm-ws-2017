import org.junit.After;
import org.junit.Before;
import service.CompostServiceImpl;
import services.CompostServiceTest;

public class PostgresCompostServiceTest extends CompostServiceTest {

    @Before
    public void setup(){

        compostService = new CompostServiceImpl();
        TestHelper.createAllTables(CompostServiceImpl.getTables());
        init();
    }
}
