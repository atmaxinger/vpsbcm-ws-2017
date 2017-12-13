package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PackingServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.ResearchServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Exitable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.MarketServiceImpl;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PostgresPackRobot {
    final static Logger logger = Logger.getLogger(PostgresPackRobot.class);

    public static void main(String[] args) throws URISyntaxException, SQLException {
        if(args.length != 1) {
            logger.fatal("You need to specify the id!");
            System.exit(0);
        }

        List<Exitable> exitables = new LinkedList<>();

        PackingServiceImpl packingService = new PackingServiceImpl();
        MarketServiceImpl marketService = new MarketServiceImpl();
        ResearchService researchService = new ResearchServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl();

        exitables.add(packingService);
        exitables.add(marketService);
        exitables.add(researchService);

        PackRobot packRobot = new PackRobot(args[0], packingService,marketService,researchService,transactionService);
        packingService.registerPackRobot(packRobot);

        Scanner scanner = new Scanner(System.in);
        scanner.next("exit");

        for(Exitable exitable : exitables) {
            exitable.setExit(true);
        }
    }
}
