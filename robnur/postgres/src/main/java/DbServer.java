import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import com.impossibl.postgres.api.jdbc.PGConnection;
import service.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DbServer {
    public static void main(String args[]) {

        createTables(CompostServiceImpl.getTables());
        createNotifyFunction(CompostServiceImpl.getTables());

        createTables(ConfigServiceImpl.getTables());
        createNotifyFunction(ConfigServiceImpl.getTables());

        createTables(MarketServiceImpl.getTables());
        createNotifyFunction(MarketServiceImpl.getTables());

        createTables(PackingServiceImpl.getTables());
        createNotifyFunction(PackingServiceImpl.getTables());

        createTables(ResearchServiceImpl.getTables());
        createNotifyFunction(ResearchServiceImpl.getTables());

        createTables(GreenhouseServiceImpl.getTables());
        createNotifyFunction(ResearchServiceImpl.getTables());

        createTables(StorageServiceImpl.getTables());
        createNotifyFunction(ResearchServiceImpl.getTables());
    }

    private static void createTables(List<String> tables){

        PGConnection connection = PostgresHelper.getConnection();

        for (String t:tables) {
            try {
                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE IF EXISTS " + t);
                statement.execute("CREATE TABLE " + t + "(ID BIGSERIAL PRIMARY KEY, DATA JSON NOT NULL)");
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createNotifyFunction(List<String> tables) {

        PGConnection connection = PostgresHelper.getConnection();

        for (String table : tables) {

            try {

                Statement statement = connection.createStatement();

                statement.execute(
                        String.format(
                                "CREATE OR REPLACE FUNCTION %s_function() RETURNS TRIGGER AS $$" +
                                        "        BEGIN" +
                                        "        IF (TG_OP = 'INSERT') THEN" +
                                        "           PERFORM pg_notify('%s_notify',TG_TABLE_NAME);" +
                                        "        END IF;" +
                                        "        RETURN NULL;" +
                                        "        END; " +
                                        "$$ LANGUAGE plpgsql;"
                                , table, table)
                );

                statement.execute(
                        String.format(
                        "CREATE TRIGGER %s_trigger " +
                                "AFTER INSERT ON %s " +
                                "FOR EACH ROW EXECUTE PROCEDURE %s_function();"
                        , table, table, table));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
