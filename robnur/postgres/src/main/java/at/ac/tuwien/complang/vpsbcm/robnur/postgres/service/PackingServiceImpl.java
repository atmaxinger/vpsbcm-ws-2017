package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PackingServiceImpl extends PackingService {
    final static Logger logger = Logger.getLogger(PackingServiceImpl.class);

    private static final String PACKING_FLOWER_TABLE = "paf";
    private static final String PACKING_VEGETABLE_TABLE = "pav";

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
    public PackingServiceImpl() {
        try {
            Listener flowerListener = new Listener(PACKING_FLOWER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    raiseFlowersChanged();
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(PACKING_VEGETABLE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    raiseVegetablesChanged();
                }
            };
            vegetableListener.start();

            listeners.add(flowerListener);
            listeners.add(vegetableListener);

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public void putFlower(Flower flower, Transaction transaction) {
        ServiceUtil.writeItem(flower,PACKING_FLOWER_TABLE, transaction);
    }

    @Override
    public void putVegetable(Vegetable vegetable, Transaction transaction) {
        ServiceUtil.writeItem(vegetable,PACKING_VEGETABLE_TABLE,transaction);
    }

    public Flower getFlower(String flowerId, Transaction transaction) {
        return ServiceUtil.getItemById(flowerId,PACKING_FLOWER_TABLE,Flower.class,transaction);
    }

    public Vegetable getVegetable(String vegetableId, Transaction transaction) {
        return ServiceUtil.getItemById(vegetableId,PACKING_VEGETABLE_TABLE,Vegetable.class,transaction);
    }

    public List<Flower> readAllFlowers(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(PACKING_FLOWER_TABLE,Flower.class);
        }
        return ServiceUtil.readAllItems(PACKING_FLOWER_TABLE,Flower.class,transaction);
    }

    public List<Vegetable> readAllVegetables(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(PACKING_VEGETABLE_TABLE,Vegetable.class);
        }
        return ServiceUtil.readAllItems(PACKING_VEGETABLE_TABLE,Vegetable.class,transaction);
    }

    @Override
    public Vegetable getVegetableByType(VegetableType type, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'cultivationInformation'->>'vegetableType'",type.name(),PACKING_VEGETABLE_TABLE,Vegetable.class,transaction);
    }

    @Override
    public Flower getFlowerByType(FlowerType type, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'cultivationInformation'->>'flowerType'",type.name(),PACKING_FLOWER_TABLE,Flower.class,transaction);
    }

    public static List<String> getTables() {
        return Arrays.asList(PACKING_FLOWER_TABLE,PACKING_VEGETABLE_TABLE);
    }

    public void registerPackRobot(PackRobot packRobot) {
        try {
            Listener flowerListener = new Listener(PACKING_FLOWER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    packRobot.tryCreateBouquet();
                    packRobot.tryCreateVegetableBasket();
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(PACKING_VEGETABLE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    packRobot.tryCreateVegetableBasket();
                    packRobot.tryCreateBouquet();
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
