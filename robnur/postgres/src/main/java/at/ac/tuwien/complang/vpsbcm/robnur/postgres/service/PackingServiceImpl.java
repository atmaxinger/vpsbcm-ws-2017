package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PackingServiceImpl extends PackingService {

    private static final String PACKING_FLOWER_TABLE = "paf";
    private static final String PACKING_VEGETABLE_TABLE = "pav";


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

        } catch (SQLException e) {
            e.printStackTrace();
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
