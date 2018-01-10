package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;

public class PGUI {
    public static void main(String[] args) {
        StorageService storageService = new StorageServiceImpl();
        GreenhouseService greenhouseService = new GreenhouseServiceImpl();
        MarketService marketService = new MarketServiceImpl();
        PackingService packingService = new PackingServiceImpl();
        ResearchService researchService = new ResearchServiceImpl();
        CompostService compostService = new CompostServiceImpl();
        ConfigService configService = new ConfigServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();
        OrderService orderService = new OrderServiceImpl();

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
