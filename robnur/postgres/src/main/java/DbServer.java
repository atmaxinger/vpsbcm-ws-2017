import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import com.impossibl.postgres.api.jdbc.PGConnection;
import service.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DbServer {
    public static void main(String args[]) {

        try
        {
            PGConnection connection = PostgresHelper.getConnection();
            Statement statement = connection.createStatement();

            createTables(CompostServiceImpl.getTables(),statement);
            //createNotifyFunction(CompostServiceImpl.getTables(),statement);

            createTables(ConfigServiceImpl.getTables(),statement);
            //createNotifyFunction(ConfigServiceImpl.getTables(),statement);

            createTables(MarketServiceImpl.getTables(),statement);
            //createNotifyFunction(MarketServiceImpl.getTables(),statement);

            createTables(PackingServiceImpl.getTables(),statement);
            //createNotifyFunction(PackingServiceImpl.getTables(),statement);

            createTables(ResearchServiceImpl.getTables(),statement);
            createNotifyFunction(ResearchServiceImpl.getTables(),statement);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void createTables(List<String> tables, Statement statement){

        for (String t:tables) {
            try {
                statement.execute("DROP TABLE IF EXISTS " + t);
                statement.execute("CREATE TABLE " + t + "(ID BIGSERIAL PRIMARY KEY, DATA JSON NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createNotifyFunction(List<String> tables, Statement statement) {

        for (String table : tables) {

            try {

                PostgresHelper.getConnection().createStatement().execute(
                        String.format(
                                "CREATE OR REPLACE FUNCTION %s_function() RETURNS TRIGGER AS $$" +
                                        "        DECLARE" +
                                        "        data json;" +
                                        "        notification json;" +
                                        "        BEGIN" +
                                        "        IF (TG_OP = 'INSERT') THEN" +
                                        "           data = row_to_json(NEW);" +
                                        "           notification = json_build_object(" +
                                        "                'table',TG_TABLE_NAME," +
                                        "                'action', TG_OP," +
                                        "                'data', data);" +
                                        "           PERFORM pg_notify('%s_notify',notification::text);" +
                                        "        END IF;" +
                                        "        RETURN NULL;" +
                                        "        END; " +
                                        "$$ LANGUAGE plpgsql;"
                                , table, table)
                );

                PostgresHelper.getConnection().createStatement().execute(String.format(
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
