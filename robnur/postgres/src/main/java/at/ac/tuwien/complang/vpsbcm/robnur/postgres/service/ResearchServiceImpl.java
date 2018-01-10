package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ResearchServiceImpl extends ResearchService {
    final static Logger logger = Logger.getLogger(ResearchServiceImpl.class);

    private static final String RESEARCH_FLOWER_TABLE = "rf";
    private static final String RESEARCH_VEGETABLE_TABLE = "rv";

    private List<Listener> listeners = new LinkedList<>();

    private boolean exit = false;

    @Override
    public synchronized boolean isExit() {
        return exit;
    }

    @Override
    public synchronized void setExit(boolean exit) {
        this.exit = exit;
        if(exit == true) {
            for(Listener listener : listeners) {
                listener.shutdown();
            }
        }
    }

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

            listeners.add(flowerListener);
            listeners.add(vegetableListener);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
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

    @Override
    public List<Flower> readAllFlowers(Transaction transaction) {
        return ServiceUtil.readAllItems(RESEARCH_FLOWER_TABLE,Flower.class);
    }

    @Override
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

            listeners.add(flowerListener);
            listeners.add(vegetableListener);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }
}
