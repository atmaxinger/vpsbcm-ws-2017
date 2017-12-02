package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.StorageController;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.*;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class GUI {
    public static void main(String[] args) throws URISyntaxException, MzsCoreException, InterruptedException {
        URI uri = new URI("xvsm://localhost:9876");

        StorageService storageService = new StorageServiceImpl(uri);
        GreenhouseService greenhouseService = new GreenhouseServiceImpl(uri);
        MarketService marketService = new MarketServiceImpl(uri);
        PackingService packingService = new PackingServiceImpl(uri);
        ResearchService researchService = new ResearchServiceImpl(uri);
        CompostService compostService = new CompostServiceImpl(uri);
        ConfigService configService = new ConfigServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);

        RobNurGUI.storageService = storageService;
        RobNurGUI.greenhouseService = greenhouseService;
        RobNurGUI.marketService = marketService;
        RobNurGUI.packingService = packingService;
        RobNurGUI.researchService = researchService;
        RobNurGUI.compostService = compostService;
        RobNurGUI.configService = configService;
        RobNurGUI.transactionService = transactionService;

        RobNurGUI robNurGUI = new RobNurGUI();
        robNurGUI.execute(args);
    }
}
