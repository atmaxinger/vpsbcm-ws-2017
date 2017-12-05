package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.StorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PackingServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
}
