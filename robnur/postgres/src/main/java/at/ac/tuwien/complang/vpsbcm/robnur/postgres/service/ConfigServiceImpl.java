package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.util.Arrays;
import java.util.List;

public class ConfigServiceImpl extends ConfigService {

    private static final String CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE = "cfpci";
    private static final String CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE = "cvpci";

    public FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    public VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class,transaction);
    }

    public void deleteFlowerPlantCultivationInformation(String id, Transaction transaction) {
        ServiceUtil.deleteItemById(id,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
    }

    public void deleteVegetablePlantCultivationInformation(String id, Transaction transaction) {
        ServiceUtil.deleteItemById(id,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
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
