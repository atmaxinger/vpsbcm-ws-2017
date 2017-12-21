package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Exitable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.*;
import org.apache.log4j.Logger;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class SpacePackRobot {
    final static Logger logger = Logger.getLogger(SpacePackRobot.class);


    public static void main(String[] args) throws URISyntaxException, MzsCoreException {
        if(args.length != 2) {
            logger.fatal("You need to specify an Id and a space uri");
            System.exit(1);
        }

        URI uri = new URI(args[1]);

        List<Exitable> exitables = new LinkedList<>();

        PackingServiceImpl packingService = new PackingServiceImpl(uri);
        MarketServiceImpl marketService = new MarketServiceImpl(uri);
        ResearchService researchService = new ResearchServiceImpl(uri);
        OrderService orderService = new OrderServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);

        PackRobot packRobot = new PackRobot(args[0], packingService,marketService,researchService,orderService,transactionService);
        packingService.registerPackRobot(packRobot);

        orderService.onFlowerOrdersChanged(p -> {
            packRobot.tryCreateBouquet();
        });

        orderService.onVegetableOrdersChanged(p -> {
            packRobot.tryCreateVegetableBasket();
        });

        exitables.add(packingService);
        exitables.add(marketService);
        exitables.add(researchService);

        Scanner scanner = new Scanner(System.in);
        scanner.next("exit");

        for(Exitable exitable : exitables) {
            exitable.setExit(true);
        }

        logger.debug("SpacePackRobot stopped");
    }
}
