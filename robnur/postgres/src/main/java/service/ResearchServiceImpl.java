package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ResearchServiceImpl extends ResearchService {

    private static final String RESEARCH_FLOWER_TABLE = "rf";
    private static final String RESEARCH_VEGETABLE_TABLE = "rv";

    public void putFlower(Flower flower) {
        ServiceUtil.writeItem(flower,RESEARCH_FLOWER_TABLE);
    }

    public void putVegetable(Vegetable vegetable) {
        ServiceUtil.writeItem(vegetable,RESEARCH_VEGETABLE_TABLE);
    }

    public void deleteFlower(Flower flower, Transaction transaction) {
        ServiceUtil.deleteItemById(flower.getId(),RESEARCH_FLOWER_TABLE,transaction);
    }

    public void deleteVegetable(Vegetable vegetable, Transaction transaction) {
        ServiceUtil.deleteItemById(vegetable.getId(),RESEARCH_VEGETABLE_TABLE,transaction);
    }

    public List<Flower> readAllFlowers(Transaction transaction) {
        return ServiceUtil.readAllItems(RESEARCH_FLOWER_TABLE,Flower.class);
    }

    public List<Vegetable> readAllVegetables(Transaction transaction) {
        return ServiceUtil.readAllItems(RESEARCH_VEGETABLE_TABLE,Vegetable.class);
    }

    public static List<String> getTables() {
        return Arrays.asList(RESEARCH_FLOWER_TABLE,RESEARCH_VEGETABLE_TABLE);
    }

    public void registerResearchRobot(ResearchRobot researchRobot) {

        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String table) {
                System.out.println("/channels/" + channelName + " " + table);

                switch (table){
                    case RESEARCH_FLOWER_TABLE:
                        researchRobot.tryUpgradeFlowerPlant();
                        break;
                    case RESEARCH_VEGETABLE_TABLE:
                        researchRobot.tryUpgradeVegetablePlant();
                        break;
                }
            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(RESEARCH_FLOWER_TABLE);
        PostgresHelper.setUpListen(RESEARCH_VEGETABLE_TABLE);
    }
}
