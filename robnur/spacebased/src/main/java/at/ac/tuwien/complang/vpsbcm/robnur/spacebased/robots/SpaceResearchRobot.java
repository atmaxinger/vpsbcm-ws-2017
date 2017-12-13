package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.CompostServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ResearchServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class SpaceResearchRobot {

    public static void main(String[] args) throws URISyntaxException, MzsCoreException {
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


        List<Notification> notifications = researchService.registerResearchRobot(researchRobot);

        Scanner scanner = new Scanner(System.in);
        scanner.next("exit");

        for (Notification n:notifications) {
            n.destroy();
        }

        System.out.println("SpacePlantAndHarvestRobot stopped");
    }
}
