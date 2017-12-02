package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.MonitoringRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class SpaceMonitoringRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException, InterruptedException {
        if(args.length != 1) {
            System.err.println("You need to specify the space uri");
            System.exit(1);
        }

        URI uri = new URI(args[0]);
        GreenhouseService greenhouseService = new GreenhouseServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);

        MonitoringRobot monitoringRobot = new MonitoringRobot(greenhouseService, transactionService);

        monitoringRobot.doStuff();
    }
}
