package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.DeliveryStorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.OrderServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PostgresHelper;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui.CustomerGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PostgresCustomerGUI {


    private static void createDb(Connection connection, String databasename) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DROP DATABASE IF EXISTS " +  databasename);
        statement.execute("CREATE DATABASE " +  databasename);
        statement.close();
    }

    private static void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        for (String t : DeliveryStorageServiceImpl.getTables()){
            statement.execute("CREATE TABLE " + t + "(ID BIGSERIAL PRIMARY KEY, DATA JSON NOT NULL)");

        }
        statement.close();
    }

    private static void createNotifyFunction(Connection connection) throws SQLException {
        for (String table : DeliveryStorageServiceImpl.getTables()) {
            Statement statement = connection.createStatement();

            statement.execute(
                    String.format(
                            "CREATE OR REPLACE FUNCTION %s_function() RETURNS TRIGGER AS $$" +
                                    "        BEGIN" +
                                    "        PERFORM pg_notify('%s_notify', TG_OP);" +
                                    "        RETURN NULL;" +
                                    "        END; " +
                                    "$$ LANGUAGE plpgsql;"
                            , table, table)
            );

            statement.execute(
                    String.format(
                            "CREATE TRIGGER %s_trigger " +
                                    "AFTER INSERT OR DELETE ON %s " +
                                    "FOR EACH ROW EXECUTE PROCEDURE %s_function();"
                            , table, table, table));

            statement.close();

        }
    }

    public static void main(String[] args) throws SQLException {
        if(args.length == 0) {
            System.err.println("You need to specify the id and a server address");
            System.exit(1);
        }

        String server = args[1];
        int port = 5432;
        String database = "customer_" + args[0];
        String url = String.format("jdbc:postgresql://%s:%d/%s", server, port, database);

        String urlForCreation = String.format("jdbc:postgresql://%s:%d/", server, port);


        createDb(PostgresHelper.getConnectionForUrl(urlForCreation), database);
        createTables(PostgresHelper.getConnectionForUrl(url));
        createNotifyFunction(PostgresHelper.getConnectionForUrl(url));


        ConfigService configService = new ConfigServiceImpl();
        OrderService orderService = new OrderServiceImpl();
        DeliveryStorageService deliveryStorageService = new DeliveryStorageServiceImpl(url);

        CustomerGUI.configService = configService;
        CustomerGUI.orderService = orderService;
        CustomerGUI.deliveryStorageService = deliveryStorageService;
        CustomerGUI.address = url;

        CustomerGUI customerGUI = new CustomerGUI();
        customerGUI.execute(args);
    }
}
