package service;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PostgresHelper {

    private static PGConnection connection;

    private static String readProperty(String property) {
        Properties prop = new Properties();
        try {
            InputStream input = ClassLoader.getSystemResourceAsStream("postgres.properties");
            prop.load(input);

            return prop.getProperty(property);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static PGConnection getConnection(){

        if(connection == null){
            PGDataSource dataSource = new PGDataSource();
            dataSource.setHost(readProperty("db.server"));
            dataSource.setPort(Integer.parseInt(readProperty("db.port")));
            dataSource.setDatabase(readProperty("db.database"));
            dataSource.setUser(readProperty("db.user"));
            dataSource.setPassword(readProperty("db.password"));

            try {
                connection = (PGConnection) dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static PGConnection getNewConnection(){

        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost(readProperty("db.server"));
        dataSource.setPort(Integer.parseInt(readProperty("db.port")));
        dataSource.setDatabase(readProperty("db.database"));
        dataSource.setUser(readProperty("db.user"));
        dataSource.setPassword(readProperty("db.password"));

        PGConnection pgConnection = null;
        try {
            pgConnection = (PGConnection) dataSource.getConnection();
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