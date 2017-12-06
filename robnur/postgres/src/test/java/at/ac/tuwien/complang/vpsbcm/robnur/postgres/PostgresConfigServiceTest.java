package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpbscm.robnur.shared.services.ConfigServiceTest;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ConfigServiceImpl;
import org.junit.Before;

public class PostgresConfigServiceTest extends ConfigServiceTest {

    @Before
    public void setup(){

        configService = new ConfigServiceImpl();
        TestHelper.createAllTables(ConfigServiceImpl.getTables());
        init();
    }
}
