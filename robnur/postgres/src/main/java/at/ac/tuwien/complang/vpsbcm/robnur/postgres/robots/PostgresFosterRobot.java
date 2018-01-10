package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.StorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.FosterRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Exitable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PostgresFosterRobot {
    final static Logger logger = Logger.getLogger(PostgresFosterRobot.class);

    public static void main(String[] args) {
        if(args.length != 1) {
            logger.fatal("You need to specify an Id");
            System.exit(1);
        }

        List<Exitable> exitables = new LinkedList<>();

        GreenhouseServiceImpl greenhouseService = new GreenhouseServiceImpl();
        StorageServiceImpl storageService = new StorageServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();

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
