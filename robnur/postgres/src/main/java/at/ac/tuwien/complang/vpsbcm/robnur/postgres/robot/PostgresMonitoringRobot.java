package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.MonitoringRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

public class PostgresMonitoringRobot {

    public static void main(String[] args) {

        GreenhouseService greenhouseService = new GreenhouseServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();

        MonitoringRobot monitoringRobot = new MonitoringRobot(greenhouseService, transactionService);

        monitoringRobot.doStuff();
    }
}
