package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PostgresHelper;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PostgresMonitoringRobot {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GreenhouseServiceImpl.class);

    private static Connection connection;
    private static Statement statement;

    private static boolean exit = false;

    public static void main(String[] args) throws SQLException {

        connection = PostgresHelper.getNewConnection("monitor_token",-1);
        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE monitor_token()");
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            connection.close();
            statement.close();
            logger.info("There is already one active monitoring robot.");
            return;
        }

        UserInputListener inputListener = new UserInputListener();
        Thread thread = new Thread(inputListener);
        thread.start();

        monitorGreenhouse();
    }

    public static void monitorGreenhouse() {

        TransactionService transactionService = new TransactionServiceImpl();

        while (!exit) {

            Transaction transaction = transactionService.beginTransaction(-1);

            try {
                Statement statement = ((TransactionImpl)transaction).getConnection().createStatement();

                statement.execute(
                        "UPDATE gvp g1 SET" +
                                "    data =" +
                                "    (SELECT jsonb (data) || jsonb_build_object('growth', " +
                                "                              ((SELECT (data->>'growth')::int " +
                                "                                      FROM gvp g3 " +
                                "                                      WHERE (g3.data->>'id')::text = (g1.data->>'id')::text) + (10))::int) " + //((random()*(1.2-0.8)+0.8) * 100))::int) " +
                                "                          || jsonb_build_object('infestation', " +
                                "                                (Select " +
                                "                                       Case (Select (random() * 100 <= ((g1.data->>'cultivationInformation')::json ->>'vulnerability')::float)) " +
                                "                                            WHEN true THEN (SELECT (g1.data->>'infestation')::float + 0.1)::float " +
                                "                                            WHEN false THEN (SELECT (g1.data->>'infestation')::float)::float " +
                                "                                       END)) " +
                                "      FROM gvp g2 " +
                                "      WHERE g1.id = g2.id);");
                logger.info("PostgresMonitoringRobot: grew vegetables");

                statement.execute(
                        "UPDATE gfp g1 SET" +
                                "    data =" +
                                "    (SELECT jsonb (data) || jsonb_build_object('growth', " +
                                "                              ((SELECT (data->>'growth')::int " +
                                "                                      FROM gfp g3 " +
                                "                                      WHERE (g3.data->>'id')::text = (g1.data->>'id')::text) + (10))::int) " + //((random()*(1.2-0.8)+0.8) * 100))::int) " +
                                "                          || jsonb_build_object('infestation', " +
                                "                                (Select " +
                                "                                       Case (Select (random() * 100 <= ((g1.data->>'cultivationInformation')::json ->>'vulnerability')::float)) " +
                                "                                            WHEN true THEN (SELECT (g1.data->>'infestation')::float + 0.1)::float " +
                                "                                            WHEN false THEN (SELECT (g1.data->>'infestation')::float)::float " +
                                "                                       END)) " +
                                "      FROM gfp g2 " +
                                "      WHERE g1.id = g2.id);");
                logger.info("PostgresMonitoringRobot: grew flowers");

                transaction.commit();

            } catch (SQLException e) {
                logger.trace("EXCEPTION", e);
                transaction.rollback();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.trace("EXCEPTION", e);
            }
        }

        try {
            statement.execute("DROP TABLE monitor_token");
        } catch (SQLException e) {
            return;
        }


    }

    private static class UserInputListener implements Runnable{

        @Override
        public void run() {
            Scanner scanner = new Scanner (System.in);

            while(!scanner.hasNext("exit")){ scanner.next();}
            exit = true;
        }
    }
}
