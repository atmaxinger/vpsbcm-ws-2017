package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;

import java.util.LinkedList;
import java.util.List;

public class ConfigService {
    public List<FlowerPlantCultivationInformation> getFlowerPlantCultivationInformation() {
        List<FlowerPlantCultivationInformation> ci = new LinkedList<>();

        FlowerPlantCultivationInformation i = new FlowerPlantCultivationInformation();
        i.setFlowerType(FlowerType.ROSE);
        i.setSoilAmount(20);
        i.setWaterAmount(250);
        i.setFertilizerAmount(1);
        i.setGrowthRate(0.25f);
        i.setHarvest(4);
        i.setUpgradeLevel(0);
        ci.add(i);

        i = new FlowerPlantCultivationInformation();
        i.setFlowerType(FlowerType.TULIP);
        i.setSoilAmount(15);
        i.setWaterAmount(375);
        i.setFertilizerAmount(2);
        i.setGrowthRate(0.5f);
        i.setHarvest(2);
        i.setUpgradeLevel(0);
        ci.add(i);

        i = new FlowerPlantCultivationInformation();
        i.setFlowerType(FlowerType.DAISY);
        i.setSoilAmount(23);
        i.setWaterAmount(237);
        i.setFertilizerAmount(1);
        i.setGrowthRate(0.25f);
        i.setHarvest(4);
        i.setUpgradeLevel(0);
        ci.add(i);

        i = new FlowerPlantCultivationInformation();
        i.setFlowerType(FlowerType.VIOLET);
        i.setSoilAmount(27);
        i.setWaterAmount(250);
        i.setFertilizerAmount(1);
        i.setGrowthRate(0.25f);
        i.setHarvest(4);
        i.setUpgradeLevel(0);
        ci.add(i);

        return ci;
    }

    public List<VegetablePlantCultivationInformation> getVegetablePlantCultivationInformation() {
        List<VegetablePlantCultivationInformation> ci = new LinkedList<>();

        VegetablePlantCultivationInformation i = new VegetablePlantCultivationInformation();
        i.setVegetableType(VegetableType.PEPPER);
        i.setSoilAmount(30);
        i.setWaterAmount(450);
        i.setFertilizerAmount(1);
        i.setGrowthRate(0.2f);
        i.setHarvest(6);
        i.setMaxNumberOfHarvests(2);
        i.setUpgradeLevel(0);
        ci.add(i);

        i = new VegetablePlantCultivationInformation();
        i.setVegetableType(VegetableType.TOMATO);
        i.setSoilAmount(25);
        i.setWaterAmount(600);
        i.setFertilizerAmount(2);
        i.setGrowthRate(0.35f);
        i.setHarvest(3);
        i.setMaxNumberOfHarvests(3);
        i.setUpgradeLevel(0);
        ci.add(i);

        i = new VegetablePlantCultivationInformation();
        i.setVegetableType(VegetableType.CARROT);
        i.setSoilAmount(30);
        i.setWaterAmount(450);
        i.setFertilizerAmount(1);
        i.setGrowthRate(0.2f);
        i.setHarvest(6);
        i.setMaxNumberOfHarvests(2);
        i.setUpgradeLevel(0);
        ci.add(i);

        i = new VegetablePlantCultivationInformation();
        i.setVegetableType(VegetableType.SALAD);
        i.setSoilAmount(30);
        i.setWaterAmount(450);
        i.setFertilizerAmount(1);
        i.setGrowthRate(0.2f);
        i.setHarvest(6);
        i.setMaxNumberOfHarvests(1);
        i.setUpgradeLevel(0);
        ci.add(i);

        return ci;
    }
}
