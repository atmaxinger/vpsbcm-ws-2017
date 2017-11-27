package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.*;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;

public class SpacePackRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException {
        URI uri = new URI("xvsm://localhost:9876");

        PackingServiceImpl packingService = new PackingServiceImpl(uri);
        MarketServiceImpl marketService = new MarketServiceImpl(uri);
        ResearchService researchService = null; // TODO add impl
        TransactionService transactionService = new TransactionServiceImpl(uri);

        PackRobot packRobot = new PackRobot(packingService,marketService,researchService,transactionService);
        packingService.registerPackRobot(packRobot);
    }
}
