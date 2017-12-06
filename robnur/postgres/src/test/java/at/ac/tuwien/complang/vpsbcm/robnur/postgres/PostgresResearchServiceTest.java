package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpbscm.robnur.shared.services.ResearchServiceTest;
import org.junit.Before;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ResearchServiceImpl;

public class PostgresResearchServiceTest extends ResearchServiceTest {
    @Before
    public void setup(){

        researchService = new ResearchServiceImpl();
        TestHelper.createAllTables(ResearchServiceImpl.getTables());
        init();
    }
}
