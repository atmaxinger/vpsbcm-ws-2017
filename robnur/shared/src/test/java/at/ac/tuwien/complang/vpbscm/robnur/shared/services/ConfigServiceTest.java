package at.ac.tuwien.complang.vpbscm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

public abstract class ConfigServiceTest {

    public ConfigService configService;

    private FlowerPlantCultivationInformation flowerPlantCultivationInformation1, flowerPlantCultivationInformation2;
    private VegetablePlantCultivationInformation vegetablePlantCultivationInformation1, vegetablePlantCultivationInformation2;

    public void init() {

        flowerPlantCultivationInformation1 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation1.setFlowerType(FlowerType.ROSE);
        flowerPlantCultivationInformation1.setSoilAmount(20);
        flowerPlantCultivationInformation1.setWaterAmount(250);
        flowerPlantCultivationInformation1.setFertilizerAmount(1);
        flowerPlantCultivationInformation1.setGrowthRate(0.25f);
        flowerPlantCultivationInformation1.setHarvest(4);
        flowerPlantCultivationInformation1.setUpgradeLevel(0);


        flowerPlantCultivationInformation2 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation2.setFlowerType(FlowerType.TULIP);
        flowerPlantCultivationInformation2.setSoilAmount(15);
        flowerPlantCultivationInformation2.setWaterAmount(375);
        flowerPlantCultivationInformation2.setFertilizerAmount(2);
        flowerPlantCultivationInformation2.setGrowthRate(0.5f);
        flowerPlantCultivationInformation2.setHarvest(2);
        flowerPlantCultivationInformation2.setUpgradeLevel(0);

        vegetablePlantCultivationInformation1 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation1.setVegetableType(VegetableType.PEPPER);
        vegetablePlantCultivationInformation1.setSoilAmount(30);
        vegetablePlantCultivationInformation1.setWaterAmount(450);
        vegetablePlantCultivationInformation1.setFertilizerAmount(1);
        vegetablePlantCultivationInformation1.setGrowthRate(0.2f);
        vegetablePlantCultivationInformation1.setHarvest(6);
        vegetablePlantCultivationInformation1.setRemainingNumberOfHarvests(2);
        vegetablePlantCultivationInformation1.setUpgradeLevel(0);

        vegetablePlantCultivationInformation2 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation2.setVegetableType(VegetableType.TOMATO);
        vegetablePlantCultivationInformation2.setSoilAmount(25);
        vegetablePlantCultivationInformation2.setWaterAmount(600);
        vegetablePlantCultivationInformation2.setFertilizerAmount(2);
        vegetablePlantCultivationInformation2.setGrowthRate(0.35f);
        vegetablePlantCultivationInformation2.setHarvest(3);
        vegetablePlantCultivationInformation2.setRemainingNumberOfHarvests(3);
        vegetablePlantCultivationInformation2.setUpgradeLevel(0);

        configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation1, null);
        configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation2, null);
        configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation1, null);
        configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation2, null);
    }

    @Test
    public void putFlowerPlantCultivationInformation_ShouldWork(){
        Assert.assertTrue(configService.readAllFlowerPlantCultivationInformation(null).size() == 2);
        Assert.assertTrue(configService.readAllFlowerPlantCultivationInformation(null).get(0).equals(flowerPlantCultivationInformation1) || configService.readAllFlowerPlantCultivationInformation(null).get(1).equals(flowerPlantCultivationInformation1));
    }

    @Test
    public void putVegetablePlantCultivationInformation_ShouldWork(){
        Assert.assertTrue(configService.readAllVegetablePlantCultivationInformation(null).size() == 2);
        Assert.assertTrue(configService.readAllVegetablePlantCultivationInformation(null).get(0).equals(vegetablePlantCultivationInformation1) || configService.readAllVegetablePlantCultivationInformation(null).get(1).equals(vegetablePlantCultivationInformation1));
    }

    @Test
    public void readAllFlowerPlantCultivationInformation_ShouldWork(){
        Assert.assertTrue(configService.readAllFlowerPlantCultivationInformation(null).size() == 2);
    }

    @Test
    public void readAllVegetablePlantCultivationInformation_ShouldWork(){
        Assert.assertTrue(configService.readAllVegetablePlantCultivationInformation(null).size() == 2);
    }
}
