package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class ResearchServiceImpl extends ResearchService {

    private static final String RESEARCH_FLOWER_TABLE = "RESEARCH_FLOWER_TABLE";
    private static final String RESEARCH_VEGETABLE_TABLE = "RESEARCH_VEGETABLE_TABLE";

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

    public void registerResearchRobot(final ResearchRobot researchRobot) {

        PGNotificationListener flowerNotificationListener = new PGNotificationListener() {
            public void notification(int i, String s, String s1) {
                researchRobot.tryUpgradeFlowerPlant();
            }
        };

        PostgresHelper.setUpNotification(flowerNotificationListener,RESEARCH_FLOWER_TABLE);

        PGNotificationListener vegetableNotificationListener = new PGNotificationListener() {
            public void notification(int i, String s, String s1) {
                researchRobot.tryUpgradeVegetablePlant();
            }
        };

        PostgresHelper.setUpNotification(vegetableNotificationListener,RESEARCH_FLOWER_TABLE);
    }
}
