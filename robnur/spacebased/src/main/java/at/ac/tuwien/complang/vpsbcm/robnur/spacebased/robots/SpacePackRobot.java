package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.MarketServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.PackingServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ResearchServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class SpacePackRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException {
        if(args.length != 2) {
            System.err.println("You need to specify an Id and a space uri");
            System.exit(1);
        }

        URI uri = new URI(args[1]);

        PackingServiceImpl packingService = new PackingServiceImpl(uri);
        MarketServiceImpl marketService = new MarketServiceImpl(uri);
        ResearchService researchService = new ResearchServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);

        PackRobot packRobot = new PackRobot(args[0], packingService,marketService,researchService,transactionService);
        packingService.registerPackRobot(packRobot);
    }
}
