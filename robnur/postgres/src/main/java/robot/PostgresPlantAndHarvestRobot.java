package robot;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import service.GreenhouseServiceImpl;
import service.PackingServiceImpl;
import service.StorageServiceImpl;
import service.TransactionServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;

public class PostgresPlantAndHarvestRobot {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        int plantTransactionTimeout = 60*1000;
        int harvestTransactionTimeout = 1000;

        StorageServiceImpl storageService = new StorageServiceImpl();
        PackingService packingService = new PackingServiceImpl();
        GreenhouseServiceImpl greenhouseService = new GreenhouseServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();


        PlantAndHarvestRobot robot = new PlantAndHarvestRobot(args[0], plantTransactionTimeout, harvestTransactionTimeout, storageService, greenhouseService, transactionService, packingService);

        greenhouseService.registerPlantAndHarvestRobot(robot);
        storageService.registerPlantAndHarvestRobot(robot);
    }
}
