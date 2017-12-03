package service;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresHelper {

    private static PGConnection connection;
    public static final String COMPOST_TABLE = "COMPOST";
    public static final String CONFIG_TABLE = "CONFIG";
    public static final String GREENHOUSE_TABLE = "GREENHOUSE";
    public static final String MARKET_TABLE = "MARKET";
    public static final String PACKING_TABLE = "PACKING";
    public static final String RESEARCH_TABLE = "RESEARCH";
    public static final String STORE_TABLE = "STORE";

    public static PGConnection getConnection(){

        if(connection == null){
            PGDataSource dataSource = new PGDataSource();
            dataSource.setHost("localhost");
            dataSource.setPort(5432);
            dataSource.setDatabase("robnur");
            dataSource.setUser("postgres");
            dataSource.setPassword("123");

            try {
                connection = (PGConnection) dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static void setUpNotification(PGNotificationListener notificationListener, String table) {
        getConnection().addNotificationListener(notificationListener);

        Statement statement = null;
        try {
            statement = getConnection().createStatement();
            statement.execute(String.format("LISTEN %s_notify",table));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
