package robot;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import service.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;

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
        TransactionService transactionService = new TransactionServiceImpl(PostgresHelper.getConnection());

        ResearchRobot researchRobot = new ResearchRobot("0", researchService, compostService, configService, transactionService);
        researchService.registerResearchRobot(researchRobot);

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }
}
