package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.CompostServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ResearchServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;

public class SpaceResearchRobot {

    public static void main(String[] args) throws URISyntaxException {
        if(args.length != 2) {
            System.err.println("You need to specify an Id and a space uri");
            System.exit(1);
        }

        URI uri = new URI(args[1]);

        ResearchServiceImpl researchService = new ResearchServiceImpl(uri);
        CompostService compostService = new CompostServiceImpl(uri);
        ConfigService configService = new ConfigServiceImpl(uri);
        TransactionService transactionService = new TransactionServiceImpl(uri);

        ResearchRobot researchRobot = new ResearchRobot(args[0], researchService,compostService,configService,transactionService);
        researchService.registerResearchRobot(researchRobot);
    }
}
