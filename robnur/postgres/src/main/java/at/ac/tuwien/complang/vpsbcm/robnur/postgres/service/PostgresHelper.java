package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.jdbc.PGDataSource;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

public class PostgresHelper {

    private static PGConnection connection;
    private static PGDataSource dataSource = null;

    private static String readProperty(String property) {
        Properties prop = new Properties();
        try {
            String home = System.getProperty("user.home");
            File f = new File(home + "/robnur/postgres.properties");

            prop.load(new FileReader(f));

            return prop.getProperty(property);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void initDataSource() {
        if(dataSource == null) {
            dataSource = new PGDataSource();
            dataSource.setHost(readProperty("db.server"));
            dataSource.setPort(Integer.parseInt(Objects.requireNonNull(readProperty("db.port"))));
            dataSource.setDatabase(readProperty("db.database"));
            dataSource.setUser(readProperty("db.user"));
            dataSource.setPassword(readProperty("db.password"));
        }
    }

    public static PGConnection getConnection(){

        if(connection == null){
            initDataSource();
            try {
                connection = (PGConnection) dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }


    public static PGConnection getNewConnection(){
        initDataSource();
        PGConnection pgConnection = null;
        try {
            // This is a workaround for a bug in the JDBC driver implementation
            try {
                pgConnection = (PGConnection) dataSource.getConnection();
            } catch (SQLException e) {
                // IGNORE
            }

            if(pgConnection == null) {
                pgConnection = (PGConnection) dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pgConnection;
    }

    public static void setUpListen(String table) {

        try {
            Statement statement = PostgresHelper.getConnection().createStatement();
            statement.execute(String.format("LISTEN %s_notify", table));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}