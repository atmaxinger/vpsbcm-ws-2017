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

    public void putFlower(Flower flower) {
        ServiceUtil.writeItem(flower,RESEARCH_FLOWER_TABLE);
    }

    public void putVegetable(Vegetable vegetable) {
        ServiceUtil.writeItem(vegetable,RESEARCH_VEGETABLE_TABLE);
    }

    public void deleteFlower(Flower flower, Transaction transaction) {
        try {
            ServiceUtil.deleteItemById(flower.getId(),RESEARCH_FLOWER_TABLE,transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVegetable(Vegetable vegetable, Transaction transaction) {
        try {
            ServiceUtil.deleteItemById(vegetable.getId(),RESEARCH_VEGETABLE_TABLE,transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        /*PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                String table = ServiceUtil.getTableName(channelName, payload);
                if(ServiceUtil.getOperation(channelName, payload) == ServiceUtil.DBOPERATION.INSERT) {
                    switch (table) {
                        case RESEARCH_FLOWER_TABLE:
                            researchRobot.tryUpgradeFlowerPlant();
                            break;
                        case RESEARCH_VEGETABLE_TABLE:
                            researchRobot.tryUpgradeVegetablePlant();
                            break;
                    }
                }
            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(RESEARCH_FLOWER_TABLE);
        PostgresHelper.setUpListen(RESEARCH_VEGETABLE_TABLE);*/
    }
}
