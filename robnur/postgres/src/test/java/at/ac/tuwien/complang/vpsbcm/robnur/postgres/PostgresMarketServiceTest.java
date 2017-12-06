package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpbscm.robnur.shared.services.MarketServiceTest;
import org.junit.Before;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.MarketServiceImpl;

public class PostgresMarketServiceTest extends MarketServiceTest {

    @Before
    public void setup(){

        marketService = new MarketServiceImpl();
        TestHelper.createAllTables(MarketServiceImpl.getTables());
        init();
    }
}
