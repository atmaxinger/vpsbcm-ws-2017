package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CompostServiceImpl extends CompostService {

    private static final String COMPOST_FLOWER_PLANT_TABLE = "cfp";
    private static final String COMPOST_VEGETABLE_PLANT_TABLE = "cvp";
    private static final String COMPOST_FLOWER_TABLE = "cf";
    private static final String COMPOST_VEGETABLE_TABLE = "cv";


    public CompostServiceImpl() {
        try {
            Listener flowerPlantListener = new Listener(COMPOST_FLOWER_PLANT_TABLE) {
                @Override
                public void onNotify() {
                    notifyFlowerPlantsChanged(readAllFlowerPlants());
                }
            };
            flowerPlantListener.start();

            Listener flowersListener = new Listener(COMPOST_FLOWER_TABLE) {
                @Override
                public void onNotify() {
                    notifyFlowersChanged(readAllFlowers());
                }
            };
            flowersListener.start();

            Listener vegetablePlantListener = new Listener(COMPOST_VEGETABLE_PLANT_TABLE) {
                @Override
                public void onNotify() {
                    notifyVegetablePlantsChanged(readAllVegetablePlants());
                }
            };
            vegetablePlantListener.start();

            Listener vegetablesListener = new Listener(COMPOST_VEGETABLE_TABLE) {
                @Override
                public void onNotify() {
                    notifyVegetablesChanged(readAllVegetables());
                }
            };
            vegetablesListener.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
