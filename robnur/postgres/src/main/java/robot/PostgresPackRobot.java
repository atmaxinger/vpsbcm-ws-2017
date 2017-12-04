package robot;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import service.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Scanner;

public class PostgresPackRobot {

    public static void main(String[] args) throws URISyntaxException, SQLException {
        // TODO jsut for testing (do not forget id below)
        /*
        if(args.length != 1) {
            System.err.println("You need to specify the id!");
        }*/

        PackingServiceImpl packingService = new PackingServiceImpl();
        MarketServiceImpl marketService = new MarketServiceImpl();
        ResearchService researchService = new ResearchServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl(PostgresHelper.getConnection());

        PackRobot packRobot = new PackRobot("0", packingService,marketService,researchService,transactionService);
        packingService.registerPackRobot(packRobot);

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

}
