import org.junit.Before;
import service.ConfigServiceImpl;
import service.MarketServiceImpl;
import services.MarketServiceTest;

public class PostgresMarketServiceTest extends MarketServiceTest {

    @Before
    public void setup(){

        marketService = new MarketServiceImpl();
        TestHelper.createAllTables(MarketServiceImpl.getTables());
        init();
    }
}
