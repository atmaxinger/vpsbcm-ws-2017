package robot;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.MonitoringRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import service.GreenhouseServiceImpl;
import service.TransactionServiceImpl;

public class PostgresMonitoringRobot {

    public static void main(String[] args) {

        GreenhouseService greenhouseService = new GreenhouseServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();

        MonitoringRobot monitoringRobot = new MonitoringRobot(greenhouseService, transactionService);

        monitoringRobot.doStuff();
    }
}
