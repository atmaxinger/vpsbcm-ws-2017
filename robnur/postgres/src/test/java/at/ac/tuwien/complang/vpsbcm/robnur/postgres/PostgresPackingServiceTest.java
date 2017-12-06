package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpbscm.robnur.shared.services.PackingServiceTest;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PackingServiceImpl;
import org.junit.Before;

public class PostgresPackingServiceTest extends PackingServiceTest {

    @Before
    public void setup(){

        packingService = new PackingServiceImpl();
        TestHelper.createAllTables(PackingServiceImpl.getTables());
        init();
    }
}
