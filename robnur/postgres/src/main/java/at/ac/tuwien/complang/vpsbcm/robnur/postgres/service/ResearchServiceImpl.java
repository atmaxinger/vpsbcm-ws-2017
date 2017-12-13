package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ResearchServiceImpl extends ResearchService {

    private static final String RESEARCH_FLOWER_TABLE = "rf";
    private static final String RESEARCH_VEGETABLE_TABLE = "rv";

    public ResearchServiceImpl() {

        try {
            Listener flowerListener = new Listener(RESEARCH_FLOWER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowersChanged(readAllFlowers(null));
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(RESEARCH_VEGETABLE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetablesChanged(readAllVegetables(null));
                }
            };
            vegetableListener.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFlower(Flower flower, Transaction transaction) {
        ServiceUtil.writeItem(flower,RESEARCH_FLOWER_TABLE, transaction);
    }

    @Override
    public void putVegetable(Vegetable vegetable, Transaction transaction) {
        ServiceUtil.writeItem(vegetable,RESEARCH_VEGETABLE_TABLE, transaction);
    }

    @Override
    public List<Flower> getAllFlowers(Transaction transaction) {
        return ServiceUtil.getAllItems(RESEARCH_FLOWER_TABLE,Flower.class,transaction);
    }

    @Override
    public List<Vegetable> getAllVegetables(Transaction transaction) {
        return ServiceUtil.getAllItems(RESEARCH_VEGETABLE_TABLE,Vegetable.class,transaction);
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

        try {
            Listener flowerListener = new Listener(RESEARCH_FLOWER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    researchRobot.tryUpgradeFlowerPlant();
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(RESEARCH_VEGETABLE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    researchRobot.tryUpgradeVegetablePlant();
                }
            };
            vegetableListener.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
