package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpbscm.robnur.shared.services.CompostServiceTest;
import org.junit.Before;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.CompostServiceImpl;

public class PostgresCompostServiceTest extends CompostServiceTest {

    @Before
    public void setup(){

        compostService = new CompostServiceImpl();
        TestHelper.createAllTables(CompostServiceImpl.getTables());
        init();
    }
}
