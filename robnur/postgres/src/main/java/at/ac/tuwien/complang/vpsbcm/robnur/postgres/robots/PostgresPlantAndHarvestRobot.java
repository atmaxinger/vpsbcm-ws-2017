package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.net.URISyntaxException;
import java.util.Scanner;

public class PostgresPlantAndHarvestRobot {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        int plantTransactionTimeout = 60*1000;
        int harvestTransactionTimeout = 1000;

        if(args.length == 0) {
            System.err.println("You need to specify the id");
            System.exit(1);
        }
        else if(args.length == 2) {
            plantTransactionTimeout = Integer.parseInt(args[1]);
        }
        else if(args.length == 3) {
            plantTransactionTimeout = Integer.parseInt(args[1]);
            harvestTransactionTimeout = Integer.parseInt(args[2]);
        }

        StorageServiceImpl storageService = new StorageServiceImpl();
        PackingService packingService = new PackingServiceImpl();
        GreenhouseServiceImpl greenhouseService = new GreenhouseServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();
        CompostService compostService = new CompostServiceImpl();


        PlantAndHarvestRobot robot = new PlantAndHarvestRobot(args[0], plantTransactionTimeout, harvestTransactionTimeout, storageService, greenhouseService, transactionService, packingService, compostService);

        greenhouseService.registerPlantAndHarvestRobot(robot);
        storageService.registerPlantAndHarvestRobot(robot);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            String command = scanner.nextLine();
            if(command.toLowerCase().equals("exit")) {
                System.exit(0);
            }
        }
    }
}
