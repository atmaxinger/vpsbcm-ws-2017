package at.ac.tuwien.complang.vpbscm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import org.junit.Assert;
import org.junit.Test;

public abstract class CompostServiceTest {

    public CompostService compostService;

    FlowerPlant flowerPlant1, flowerPlant2;
    Flower flower1, flower2;
    VegetablePlant vegetablePlant1, vegetablePlant2;
    Vegetable vegetable1, vegetable2;

    public void init(){
        FlowerPlantCultivationInformation flowerPlantCultivationInformation1 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation1.setFlowerType(FlowerType.ROSE);
        flowerPlantCultivationInformation1.setGrowthRate(0.2f);

        FlowerPlantCultivationInformation flowerPlantCultivationInformation2 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation2.setFlowerType(FlowerType.TULIP);
        flowerPlantCultivationInformation2.setGrowthRate(0.2f);

        flowerPlant1 = new FlowerPlant();
        flowerPlant1.setCultivationInformation(flowerPlantCultivationInformation1);

        flowerPlant2 = new FlowerPlant();
        flowerPlant2.setCultivationInformation(flowerPlantCultivationInformation2);

        flower1 = new Flower();
        flower1.setParentFlowerPlant(flowerPlant1);

        flower2 = new Flower();
        flower2.setParentFlowerPlant(flowerPlant2);

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation1 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation1.setVegetableType(VegetableType.CARROT);
        vegetablePlantCultivationInformation1.setGrowthRate(0.2f);

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation2 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation2.setVegetableType(VegetableType.SALAD);
        vegetablePlantCultivationInformation2.setGrowthRate(0.2f);


        vegetablePlant1 = new VegetablePlant();
        vegetablePlant1.setCultivationInformation(vegetablePlantCultivationInformation1);

        vegetablePlant2 = new VegetablePlant();
        vegetablePlant2.setCultivationInformation(vegetablePlantCultivationInformation2);

        vegetable1 = new Vegetable();
        vegetable1.setParentVegetablePlant(vegetablePlant1);

        vegetable2 = new Vegetable();
        vegetable2.setParentVegetablePlant(vegetablePlant2);

        compostService.putFlowerPlant(flowerPlant1, null);
        compostService.putFlowerPlant(flowerPlant2, null);
        compostService.putFlower(flower1, null);
        compostService.putFlower(flower2, null);
        compostService.putVegetablePlant(vegetablePlant1, null);
        compostService.putVegetablePlant(vegetablePlant2, null);
        compostService.putVegetable(vegetable1, null);
        compostService.putVegetable(vegetable2, null);
    }

    @Test
    public void putFlowerPlant_ShouldWork(){
        Assert.assertTrue(compostService.readAllFlowerPlants().size() == 2);
        Assert.assertTrue(compostService.readAllFlowerPlants().get(0).equals(flowerPlant1) || compostService.readAllFlowerPlants().get(1).equals(flowerPlant1));
    }

    @Test
    public void putVegetablePlant_ShouldWork(){
        Assert.assertTrue(compostService.readAllVegetablePlants().size() == 2);
        Assert.assertTrue(compostService.readAllVegetablePlants().get(0).equals(vegetablePlant1) || compostService.readAllVegetablePlants().get(1).equals(vegetablePlant1));
    }

    @Test
    public void putFlower_ShouldWork(){
        Assert.assertTrue(compostService.readAllFlowers().size() == 2);
        Assert.assertTrue(compostService.readAllFlowers().get(0).equals(flower1) || compostService.readAllFlowers().get(1).equals(flower1));
    }

    @Test
    public void putVegetable_ShouldWork(){
        Assert.assertTrue(compostService.readAllFlowers().size() == 2);
        Assert.assertTrue(compostService.readAllFlowers().get(0).equals(flower1) || compostService.readAllFlowers().get(1).equals(flower1));
    }

    @Test
    public void readAllFlowerPlants_ShouldWork(){
        Assert.assertTrue(compostService.readAllFlowerPlants().size() == 2);
    }

    @Test
    public void readAllVegetablePlants_ShouldWork(){
        Assert.assertTrue(compostService.readAllVegetablePlants().size() == 2);
    }

    @Test
    public void readAllFlowers_ShouldWork(){
        Assert.assertTrue(compostService.readAllFlowers().size() == 2);
    }

    @Test
    public void readAllVegetables_ShouldWork(){
        Assert.assertTrue(compostService.readAllVegetables().size() == 2);
    }
}
