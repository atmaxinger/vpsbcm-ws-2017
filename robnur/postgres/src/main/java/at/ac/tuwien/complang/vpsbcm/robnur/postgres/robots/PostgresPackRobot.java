package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PackingServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ResearchServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.MarketServiceImpl;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Scanner;

public class PostgresPackRobot {

    public static void main(String[] args) throws URISyntaxException, SQLException {
        if(args.length != 1) {
            System.err.println("You need to specify the id!");
            System.exit(0);
        }

        PackingServiceImpl packingService = new PackingServiceImpl();
        MarketServiceImpl marketService = new MarketServiceImpl();
        ResearchService researchService = new ResearchServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();

        PackRobot packRobot = new PackRobot(args[0], packingService,marketService,researchService,transactionService);
        packingService.registerPackRobot(packRobot);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            String command = scanner.nextLine();
            if(command.toLowerCase().equals("exit")) {
                System.exit(0);
            }
        }
    }
}
