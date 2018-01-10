package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.FosterRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.*;
import org.apache.log4j.Logger;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class SpaceFosterRobot {
    final static Logger logger = Logger.getLogger(SpaceFosterRobot.class);

    public static void main(String[] args) throws URISyntaxException, InterruptedException, MzsCoreException {
        if(args.length != 2) {
            logger.fatal("You need to specify an Id and a space uri");
            System.exit(1);
        }

        URI uri = new URI(args[1]);

        List<Exitable> exitables = new LinkedList<>();

        GreenhouseServiceImpl greenhouseService = new GreenhouseServiceImpl(uri);
        StorageServiceImpl storageService = new StorageServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);

        FosterRobot robot = new FosterRobot(args[0], greenhouseService, storageService, transactionService);
        storageService.registerFosterRobot(robot);
        greenhouseService.registerFosterRobot(robot);
        robot.foster();

        exitables.add(greenhouseService);
        exitables.add(storageService);

        Scanner scanner = new Scanner(System.in);
        scanner.next("exit");

        for(Exitable exitable : exitables) {
            exitable.setExit(true);
        }

        logger.debug("Foster robot stopped");
    }
}
