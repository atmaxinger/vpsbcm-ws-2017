package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PostgresHelper;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.MonitoringRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

public class PostgresMonitoringRobot {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GreenhouseServiceImpl.class);


    public static void main(String[] args) {
        //GreenhouseService greenhouseService = new GreenhouseServiceImpl();

        //MonitoringRobot monitoringRobot = new MonitoringRobot(greenhouseService, transactionService);
        //monitoringRobot.monitorGreenhouse();
        monitorGreenhouse();


    }

    public static void monitorGreenhouse() {

        TransactionService transactionService = new TransactionServiceImpl();

        while (true) {

            Transaction transaction = transactionService.beginTransaction(-1);

            try {
                Statement statement = ((TransactionImpl)transaction).getConnection().createStatement();

                statement.execute(
                        "UPDATE gvp g1 SET " +
                        "    data = (SELECT jsonb (data) || " +
                        "    jsonb_build_object('growth', " +
                        "    ((SELECT (data->>'growth')::int from gvp g3 WHERE (g3.data->>'id')::text = (g1.data->>'id')::text) + ((random()*(1.2-0.8)+0.8) * 100))::int) " +
                        "    FROM gvp g2 " +
                        "    WHERE g1.id = g2.id);");

                transaction.commit();

                logger.info("updated vegetables");
            } catch (SQLException e) {
                e.printStackTrace();
                transaction.rollback();
            }



            try {
                logger.info("before sleep");
                Thread.sleep(2000);
                logger.info("after sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
