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

            prop.load(new FileReader(f));

            return prop.getProperty(property);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized static Connection getNewConnection(String reason){
        String server = readProperty("db.server");
        int port = Integer.parseInt(readProperty("db.port"));
        String database = readProperty("db.database");
        String user = readProperty("db.user");
        String password = readProperty("db.password");

        String url = String.format("jdbc:postgresql://%s:%d/%s", server, port, database);
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        try {
            Connection connection = DriverManager.getConnection(url, props);
            //logger.debug("NEW CONNECTION: " + connection + " reason: " + reason);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}