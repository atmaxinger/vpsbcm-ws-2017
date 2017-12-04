package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class PackingServiceImpl extends PackingService {

    private static final String PACKING_FLOWER_TABLE = "paf";
    private static final String PACKING_VEGETABLE_TABLE = "pav";

    public void putFlower(Flower flower) {
        ServiceUtil.writeItem(flower,PACKING_FLOWER_TABLE);
    }

    public void putVegetable(Vegetable vegetable) {
        ServiceUtil.writeItem(vegetable,PACKING_VEGETABLE_TABLE);
    }

    public Flower getFlower(String flowerId, Transaction transaction) {
        return ServiceUtil.getItemById(flowerId,PACKING_FLOWER_TABLE,Flower.class,transaction);
    }

    public Vegetable getVegetable(String vegetableId, Transaction transaction) {
        return ServiceUtil.getItemById(vegetableId,PACKING_VEGETABLE_TABLE,Vegetable.class,transaction);
    }

    public List<Flower> readAllFlowers(Transaction transaction) {
        return ServiceUtil.readAllItems(PACKING_FLOWER_TABLE,Flower.class,transaction);
    }

    public List<Vegetable> readAllVegetables(Transaction transaction) {
        return ServiceUtil.readAllItems(PACKING_VEGETABLE_TABLE,Vegetable.class,transaction);
    }

    public static List<String> getTables() {
        return Arrays.asList(PACKING_FLOWER_TABLE,PACKING_VEGETABLE_TABLE);
    }

    public void registerPackRobot(PackRobot packRobot) {

        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String table) {

                System.out.println("/channels/" + channelName + " " + table);

                switch (table){
                    case PACKING_FLOWER_TABLE:
                        System.out.println("PACKING_FLOWER_TABLE");
                        break;
                    case PACKING_VEGETABLE_TABLE:
                        System.out.println("PACKING_VEGETABLE_TABLE");
                        break;
                }
            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(PACKING_FLOWER_TABLE);
        PostgresHelper.setUpListen(PACKING_VEGETABLE_TABLE);
    }
}
