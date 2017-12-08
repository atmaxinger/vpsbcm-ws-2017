package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import org.postgresql.PGConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

public class PostgresHelper {

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

    public static Connection getNewConnection(){

        String url = "jdbc:postgresql://localhost/robnur";
        Properties props = new Properties();
        props.setProperty("user","tobiaskain");
        props.setProperty("password","");
        try {
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setUpListen(String table) {

        try {
            Statement statement = PostgresHelper.getNewConnection().createStatement();
            statement.execute(String.format("LISTEN %s_notify", table));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}