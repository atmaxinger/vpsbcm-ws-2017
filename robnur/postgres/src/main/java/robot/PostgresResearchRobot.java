package robot;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Scanner;

public class PostgresResearchRobot {
    public static void main(String[] args) throws URISyntaxException {
        if(args.length != 1) {
            System.err.println("You need to specify the id!");
        }

        ResearchServiceImpl researchService = new ResearchServiceImpl();
        CompostService compostService = new CompostServiceImpl();
        ConfigService configService = new ConfigServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl(PostgresHelper.getConnection());

        ResearchRobot researchRobot = new ResearchRobot(args[0], researchService, compostService, configService, transactionService);
        researchService.registerResearchRobot(researchRobot);

        Scanner scanner = new Scanner(System.in);

        while (!scanner.next().equals("exit")){

        }

        try {
            Flower flower = new Flower();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(flower);
            PostgresHelper.getConnection().createStatement().execute(String.format("INSERT INTO RESEARCH_FLOWER_TABLE (data) VALUES (%s)",json));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        while (!scanner.next().equals("exit")){

        }
    }
}
