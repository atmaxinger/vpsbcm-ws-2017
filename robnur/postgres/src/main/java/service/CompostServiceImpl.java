package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import java.util.Arrays;
import java.util.List;

public class CompostServiceImpl extends CompostService {

    private static final String COMPOST_FLOWER_PLANT_TABLE = "cfp";
    private static final String COMPOST_VEGETABLE_PLANT_TABLE = "cvp";
    private static final String COMPOST_FLOWER_TABLE = "cf";
    private static final String COMPOST_VEGETABLE_TABLE = "cv";


    public CompostServiceImpl() {
        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                String table = ServiceUtil.getTableName(channelName, payload);
                switch (table) {
                    case COMPOST_FLOWER_PLANT_TABLE:
                        notifyFlowerPlantsChanged(readAllFlowerPlants());
                        break;
                    case COMPOST_VEGETABLE_PLANT_TABLE:
                        notifyVegetablePlantsChanged(readAllVegetablePlants());
                        break;
                    case COMPOST_FLOWER_TABLE:
                        notifyFlowersChanged(readAllFlowers());
                        break;
                    case COMPOST_VEGETABLE_TABLE:
                        notifyVegetablesChanged(readAllVegetables());
                        break;
                }

            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(COMPOST_FLOWER_PLANT_TABLE);
        PostgresHelper.setUpListen(COMPOST_VEGETABLE_PLANT_TABLE);
        PostgresHelper.setUpListen(COMPOST_FLOWER_TABLE);
        PostgresHelper.setUpListen(COMPOST_VEGETABLE_TABLE);
    }

    public void putFlowerPlant(FlowerPlant flowerPlant) {
        ServiceUtil.writeItem(flowerPlant, COMPOST_FLOWER_PLANT_TABLE);
    }

    public void putVegetablePlant(VegetablePlant vegetablePlant) {
        ServiceUtil.writeItem(vegetablePlant, COMPOST_VEGETABLE_PLANT_TABLE);
    }

    public void putFlower(Flower flower) {
        ServiceUtil.writeItem(flower, COMPOST_FLOWER_TABLE);
    }

    public void putVegetable(Vegetable vegetable) {
        ServiceUtil.writeItem(vegetable, COMPOST_VEGETABLE_TABLE);
    }

    public List<FlowerPlant> readAllFlowerPlants() {
        return ServiceUtil.readAllItems(COMPOST_FLOWER_PLANT_TABLE,FlowerPlant.class);
    }

    public List<VegetablePlant> readAllVegetablePlants() {
        return ServiceUtil.readAllItems(COMPOST_VEGETABLE_PLANT_TABLE,VegetablePlant.class);
    }

    public List<Flower> readAllFlowers() {
        return ServiceUtil.readAllItems(COMPOST_FLOWER_TABLE,Flower.class);
    }

    public List<Vegetable> readAllVegetables() {
        return ServiceUtil.readAllItems(COMPOST_VEGETABLE_TABLE,Vegetable.class);
    }

    public static List<String> getTables(){
        return Arrays.asList(COMPOST_FLOWER_PLANT_TABLE,COMPOST_FLOWER_TABLE,COMPOST_VEGETABLE_PLANT_TABLE,COMPOST_VEGETABLE_TABLE);
    }
}
