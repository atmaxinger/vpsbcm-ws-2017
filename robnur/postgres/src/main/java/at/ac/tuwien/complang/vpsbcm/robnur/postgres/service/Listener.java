package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import javax.management.Notification;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.function.Function;

abstract class Listener extends Thread {
    final static Logger logger = Logger.getLogger(ConfigServiceImpl.class);

    private Connection conn;
    private Method method;
    private Object robot;

    boolean shouldRun = true;

    public enum DBMETHOD { INSERT, DELETE, UNKNOWN }

    Listener(String listenerName) throws SQLException {
        conn = PostgresHelper.getNewConnection("create listener",-1);
        Statement stmt = conn.createStatement();
        stmt.execute(String.format("LISTEN %s_notify", listenerName));
        stmt.close();
    }

    Listener(String listenerName, String foreignDbUrl) throws SQLException {
        conn = PostgresHelper.getConnectionForUrl(foreignDbUrl);
        Statement stmt = conn.createStatement();
        stmt.execute(String.format("LISTEN %s_notify", listenerName));
        stmt.close();
    }

    public void run() {
        while (shouldRun) {
            try {
                // issue a dummy query to contact the backend
                // and receive any pending notifications.
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();

                PGNotification notifications[] = ((PGConnection) conn).getNotifications();
                if (notifications != null) {
                    for(PGNotification notification : notifications) {
                        logger.debug(String.format("notification on %s for %s", notification.getName(), notification.getParameter()));
                        DBMETHOD dbmethod = DBMETHOD.UNKNOWN;
                        if(notification.getParameter().toLowerCase().equals("insert")) {
                            dbmethod = DBMETHOD.INSERT;
                        } else if(notification.getParameter().toLowerCase().equals("delete")) {
                            dbmethod = DBMETHOD.DELETE;
                        } else if(notification.getParameter().toLowerCase().equals("update")) {
                            dbmethod = DBMETHOD.INSERT;
                        }

                        onNotify(notification.getPID(), dbmethod);
                    }
                }

                // wait a while before checking again for new
                // notifications
                Thread.sleep(100);
            } catch (SQLException | InterruptedException e) {
                logger.trace("EXCEPTION", e);
            }
        }
    }

    public void shutdown() {
        shouldRun = false;
    }

    /**
     * Implements the action that shall be executed in the case of a certain db method.
     *
     * @param pid The ProcessId of the notification.
     * @param method The data base method that was executed.
     */
    public abstract void onNotify(int pid, DBMETHOD method);
}