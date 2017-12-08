package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ConfigServiceImpl extends ConfigService {

    private static final String CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE = "cfpci";
    private static final String CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE = "cvpci";

    public ConfigServiceImpl() {
        try {
            Listener flowerPlantListener = new Listener(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowerCultivationInformationChanged(readAllFlowerPlantCultivationInformation(null));
                }
            };
            flowerPlantListener.start();

            Listener flowersListener = new Listener(CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetableCultivationInformationChanged(readAllVegetablePlantCultivationInformation(null));
                }
            };
            flowersListener.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    @Override
    public FlowerPlantCultivationInformation readFlowerPlantCultivationInformation(FlowerType flowerType, Transaction transaction) {
        return ServiceUtil.readItemByParameter("flowerType",flowerType.name(),CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    public VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class,transaction);
    }

    @Override
    public VegetablePlantCultivationInformation readVegetablePlantCultivationInformation(VegetableType vegetableType, Transaction transaction) {
        return ServiceUtil.readItemByParameter("flowerType",vegetableType.name(),CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class,transaction);
    }

    public void deleteFlowerPlantCultivationInformation(String id, Transaction transaction) {
        try {
            ServiceUtil.deleteItemById(id,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVegetablePlantCultivationInformation(String id, Transaction transaction) {
        try {
            ServiceUtil.deleteItemById(id,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void putFlowerPlantCultivationInformation(FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction) {
        ServiceUtil.writeItem(flowerPlantCultivationInformation,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
    }

    public void putVegetablePlantCultivationInformation(VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction) {
        ServiceUtil.writeItem(vegetablePlantCultivationInformation,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
    }

    public List<FlowerPlantCultivationInformation> readAllFlowerPlantCultivationInformation(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class);
        }
        return ServiceUtil.readAllItems(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    public List<VegetablePlantCultivationInformation> readAllVegetablePlantCultivationInformation(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class);
        }
        return ServiceUtil.readAllItems(CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class,transaction);
    }

    public static List<String> getTables() {
        return Arrays.asList(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE);
    }
}
