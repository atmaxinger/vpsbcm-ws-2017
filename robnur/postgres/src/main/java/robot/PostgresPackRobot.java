package robot;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import service.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
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
        TransactionService transactionService = new TransactionServiceImpl();

        PackRobot packRobot = new PackRobot("0", packingService,marketService,researchService,transactionService);
        packingService.registerPackRobot(packRobot);

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

}
