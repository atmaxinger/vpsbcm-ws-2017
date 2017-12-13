package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.*;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class SpacePlantAndHarvestRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException, InterruptedException {
        int plantTransactionTimeout = 60*1000;
        int harvestTransactionTimeout = 1000;

        if(args.length < 2) {
            System.err.println("You need to specify an Id and the space uri, optionally you can specify the plant timeout (in ms) and the harvest timeout (in ms)");
            System.exit(1);
        }
        else if(args.length == 3) {
            plantTransactionTimeout = Integer.parseInt(args[2]);
        }
        else if(args.length == 4) {
            plantTransactionTimeout = Integer.parseInt(args[2]);
            harvestTransactionTimeout = Integer.parseInt(args[3]);
        }

        System.out.println(String.format("Starting with plant timeout: %d", plantTransactionTimeout));
        System.out.println(String.format("Starting with harvest timeout: %d", harvestTransactionTimeout));

        URI uri = new URI(args[1]);
        StorageServiceImpl storageService = new StorageServiceImpl(uri);
        PackingService packingService = new PackingServiceImpl(uri);
        GreenhouseServiceImpl greenhouseService = new GreenhouseServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);
        CompostService compostService = new CompostServiceImpl(uri);

        PlantAndHarvestRobot robot = new PlantAndHarvestRobot(args[0], -1, -1, storageService, greenhouseService, transactionService, packingService, compostService);

        List<Notification> notifications = greenhouseService.registerPlantAndHarvestRobot(robot);
        notifications.addAll(storageService.registerPlantAndHarvestRobot(robot));

        Scanner scanner = new Scanner(System.in);
        scanner.next("exit");

        for (Notification n:notifications) {
            n.destroy();
        }

        System.out.println("SpacePlantAndHarvestRobot stopped");
    }
}
