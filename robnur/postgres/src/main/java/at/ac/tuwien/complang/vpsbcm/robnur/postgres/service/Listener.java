package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.PGConnection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.function.Function;

abstract class Listener extends Thread {

    private Connection conn;
    private Method method;
    private Object robot;

    Listener(String listenerName) throws SQLException {
        conn = PostgresHelper.getNewConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(String.format("LISTEN %s_notify", listenerName));
        stmt.close();
    }

    public void run() {
        while (true) {
            try {
                // issue a dummy query to contact the backend
                // and receive any pending notifications.
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();

                org.postgresql.PGNotification notifications[] = ((PGConnection) conn).getNotifications();
                if (notifications != null) {
                    onNotify();
                }

                // wait a while before checking again for new
                // notifications
                Thread.sleep(500);
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    abstract public void onNotify();

}