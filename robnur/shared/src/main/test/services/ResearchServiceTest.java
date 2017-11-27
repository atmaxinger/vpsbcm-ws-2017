package services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import org.junit.Assert;
import org.junit.Test;

public abstract class ResearchServiceTest {

    public ResearchService researchService;

    FlowerPlant flowerPlant1, flowerPlant2;
    Flower flower1, flower2;
    VegetablePlant vegetablePlant1, vegetablePlant2;
    Vegetable vegetable1, vegetable2;

    public void init(){
        FlowerPlantCultivationInformation flowerPlantCultivationInformation1 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation1.setFlowerType(FlowerType.ROSE);

        FlowerPlantCultivationInformation flowerPlantCultivationInformation2 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation2.setFlowerType(FlowerType.TULIP);

        FlowerPlant flowerPlant1 = new FlowerPlant();
        flowerPlant1.setCultivationInformation(flowerPlantCultivationInformation1);

        FlowerPlant flowerPlant2 = new FlowerPlant();
        flowerPlant2.setCultivationInformation(flowerPlantCultivationInformation2);

        flower1 = new Flower();
        flower1.setParentFlowerPlant(flowerPlant1);

        flower2 = new Flower();
        flower2.setParentFlowerPlant(flowerPlant2);

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation1 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation1.setVegetableType(VegetableType.CARROT);

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation2 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation2.setVegetableType(VegetableType.SALAD);

        VegetablePlant vegetablePlant1 = new VegetablePlant();
        vegetablePlant1.setCultivationInformation(vegetablePlantCultivationInformation1);

        VegetablePlant vegetablePlant2 = new VegetablePlant();
        vegetablePlant2.setCultivationInformation(vegetablePlantCultivationInformation2);

        vegetable1 = new Vegetable();
        vegetable1.setParentVegetablePlant(vegetablePlant1);

        vegetable2 = new Vegetable();
        vegetable2.setParentVegetablePlant(vegetablePlant2);

        researchService.putFlower(flower1);
        researchService.putFlower(flower2);
        researchService.putVegetable(vegetable1);
        researchService.putVegetable(vegetable2);
    }


    @Test
    public void putFlower_ShouldWork(){
        Assert.assertTrue(researchService.readAllFlowers().size() == 2);
        Assert.assertTrue(researchService.readAllFlowers().get(0).equals(flower1) || researchService.readAllFlowers().get(1).equals(flower1));
    }

    @Test
    public void putVegetable_ShouldWork(){
        Assert.assertTrue(researchService.readAllFlowers().size() == 2);
        Assert.assertTrue(researchService.readAllFlowers().get(0).equals(flower1) || researchService.readAllFlowers().get(1).equals(flower1));
    }

    @Test
    public void readAllFlowers_ShouldWork(){
        Assert.assertTrue(researchService.readAllFlowers().size() == 2);
    }

    @Test
    public void readAllVegetables_ShouldWork(){
        Assert.assertTrue(researchService.readAllVegetables().size() == 2);
    }
}
