import org.junit.Before;
import service.PackingServiceImpl;
import service.ResearchServiceImpl;
import services.ResearchServiceTest;

public class PostgresResearchServiceTest extends ResearchServiceTest {
    @Before
    public void setup(){

        researchService = new ResearchServiceImpl();
        //TestHelper.createAllTables(ResearchServiceImpl.getTables());
        init();
    }
}
