package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ResearchServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.CompostServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Scanner;

public class PostgresResearchRobot {
    public static void main(String[] args) throws URISyntaxException, SQLException {
        // TODO jsut for testing (do not forget id below)
        /*
        if(args.length != 1) {
            System.err.println("You need to specify the id!");
        }*/

        ResearchServiceImpl researchService = new ResearchServiceImpl();
        CompostService compostService = new CompostServiceImpl();
        ConfigService configService = new ConfigServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();

        ResearchRobot researchRobot = new ResearchRobot("0", researchService, compostService, configService, transactionService);
        researchService.registerResearchRobot(researchRobot);

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }
}
