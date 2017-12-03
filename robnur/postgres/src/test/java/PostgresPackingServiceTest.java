import org.junit.Before;
import service.MarketServiceImpl;
import service.PackingServiceImpl;
import services.PackingServiceTest;

public class PostgresPackingServiceTest extends PackingServiceTest {

    @Before
    public void setup(){

        packingService = new PackingServiceImpl();
        TestHelper.createAllTables(PackingServiceImpl.getTables());
        init();
    }
}
