package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TranscationService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.PackingServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class SpacePlantAndHarvestRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException {
        URI uri = new URI("xvsm://localhost:9876");
        StorageService storageService = new StorageServiceImpl(uri);
        PackingService packingService = new PackingServiceImpl(uri);
        GreenhouseService greenhouseService = new GreenhouseServiceImpl(uri);
        TranscationService transactionService = new TransactionServiceImpl(uri);


        PlantAndHarvestRobot robot = new PlantAndHarvestRobot(storageService, greenhouseService, transactionService, packingService);

        robot.doStuff();
    }
}
