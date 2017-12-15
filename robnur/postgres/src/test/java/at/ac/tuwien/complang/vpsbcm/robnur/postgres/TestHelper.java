package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PostgresHelper;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ResearchServiceImpl;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestHelper {
    final static Logger logger = Logger.getLogger(TestHelper.class);

    public static void createAllTables(List<String> tables){
        for (String t:tables) {
            try {
                createTable(t,PostgresHelper.getNewConnection("create all tables",-1).createStatement());
            } catch (SQLException e) {
                logger.trace("EXCEPTION", e);
            }
        }
    }

    private static void createTable(String tableName, Statement statement){
        try {
            statement.execute("DROP TABLE IF EXISTS " + tableName);
            statement.execute("CREATE TABLE " + tableName + "(ID BIGSERIAL PRIMARY KEY, DATA JSON NOT NULL)");
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }
}
