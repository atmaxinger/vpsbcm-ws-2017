package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.PackingServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class SpacePlantAndHarvestRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException, InterruptedException {
        if(args.length != 2) {
            System.err.println("You need to specify an Id and a space uri");
            System.exit(1);
        }

        URI uri = new URI(args[1]);
        StorageServiceImpl storageService = new StorageServiceImpl(uri);
        PackingService packingService = new PackingServiceImpl(uri);
        GreenhouseServiceImpl greenhouseService = new GreenhouseServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);


        PlantAndHarvestRobot robot = new PlantAndHarvestRobot(storageService, greenhouseService, transactionService, packingService);
        robot.setId(args[0]);

        greenhouseService.registerPlantAndHarvestRobot(robot);
        storageService.registerPlantAndHarvestRobot(robot);
    }
}
