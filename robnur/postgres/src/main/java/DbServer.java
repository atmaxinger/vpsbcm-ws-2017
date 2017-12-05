import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
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
        createNotifyFunction(GreenhouseServiceImpl.getTables());

        createTables(StorageServiceImpl.getTables());
        createNotifyFunction(StorageServiceImpl.getTables());

        StorageService storageService = new StorageServiceImpl();
        storageService.putWater(new Water());

        createWaterTrigger("sw");
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

    private static void createWaterTrigger(String waterTable) {
        PGConnection connection = PostgresHelper.getConnection();

        Statement statement = null;
        try {
            statement = connection.createStatement();

            statement.execute(
                    String.format("CREATE OR REPLACE FUNCTION put_back_water() RETURNS TRIGGER AS $$" +
                            " BEGIN " +
                            " PERFORM pg_sleep(1); " +
                            " INSERT INTO %s(data) VALUES('{\"amount\":0,\"id\":\"49c660b6-b9e9-4a47-9922-2bfeefaef67c\"}'); " +
                            " RETURN NULL; " +
                            " END; " +
                            " $$ LANGUAGE plpgsql;",
                            waterTable
                    )
            );

            statement.execute(
                    String.format(
                            "CREATE TRIGGER water_trigger " +
                                    "AFTER DELETE ON %s " +
                                    "EXECUTE PROCEDURE put_back_water();"
                            , waterTable));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
