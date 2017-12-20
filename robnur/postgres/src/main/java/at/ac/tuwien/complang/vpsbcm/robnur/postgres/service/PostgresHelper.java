package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;
import org.postgresql.PGConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

public class PostgresHelper {

    final static Logger logger = Logger.getLogger(PostgresHelper.class);


    private synchronized static String readProperty(String property) {
        Properties prop = new Properties();
        try {
            String home = System.getProperty("user.home");
            File f = new File(home + "/robnur/postgres.properties");
            FileReader reader = new FileReader(f);

            prop.load(reader);

            reader.close();


            return prop.getProperty(property);
        } catch (IOException e) {
            logger.trace("EXCEPTION", e);
        }

        return null;
    }

    public synchronized static Connection getNewConnection(String reason, int timeoutMillis){
        String server = readProperty("db.server");
        int port = Integer.parseInt(readProperty("db.port"));
        String database = readProperty("db.database");
        String user = readProperty("db.user");
        String password = readProperty("db.password");

        String url = String.format("jdbc:postgresql://%s:%d/%s", server, port, database);
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        if(timeoutMillis > 0) {
            props.setProperty("socketTimeout", ""+timeoutMillis/1000);
        }
        try {
            Connection connection = DriverManager.getConnection(url, props);
            //logger.debug("NEW CONNECTION: " + connection + " reason: " + reason);
            return connection;
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
        return null;
    }

    public synchronized static Connection getConnectionForUrl(String connectionString) {
        String user = PostgresHelper.readProperty("db.user");
        String password = PostgresHelper.readProperty("db.password");

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);

        try {
            Connection connection = DriverManager.getConnection(connectionString, props);
            return connection;
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
        return null;
    }
}