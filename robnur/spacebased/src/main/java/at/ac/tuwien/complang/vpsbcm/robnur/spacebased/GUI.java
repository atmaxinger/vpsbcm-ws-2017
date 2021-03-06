package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;
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
        OrderService orderService = new OrderServiceImpl(uri);

        RobNurGUI.storageService = storageService;
        RobNurGUI.greenhouseService = greenhouseService;
        RobNurGUI.marketService = marketService;
        RobNurGUI.packingService = packingService;
        RobNurGUI.researchService = researchService;
        RobNurGUI.compostService = compostService;
        RobNurGUI.configService = configService;
        RobNurGUI.transactionService = transactionService;
        RobNurGUI.orderService = orderService;

        RobNurGUI robNurGUI = new RobNurGUI();
        robNurGUI.execute(args);
    }
}
