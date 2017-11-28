package services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import org.junit.Assert;
import org.junit.Test;


public abstract class PackingServiceTest {

    public PackingService packingService;

    private Flower flower1, flower2;
    private Vegetable vegetable1, vegetable2;

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

        packingService.putFlower(flower1);
        packingService.putFlower(flower2);

        packingService.putVegetable(vegetable1);
        packingService.putVegetable(vegetable2);
    }

    @Test
    public void putFlower_ShouldWork(){
        Assert.assertTrue(packingService.readAllFlowers(null).size() == 2);
        Assert.assertTrue(packingService.readAllFlowers(null).get(0).equals(flower1) && packingService.readAllFlowers(null).get(1).equals(flower2));  // FIFO order
    }

    @Test
    public void putVegetable_ShouldWork(){
        Assert.assertTrue(packingService.readAllVegetables(null).size() == 2);
        Assert.assertTrue(packingService.readAllVegetables(null).get(0).equals(vegetable1) && packingService.readAllVegetables(null).get(1).equals(vegetable2));  // FIFO order
    }

    @Test
    public void getFlower_ShouldWork(){
        Assert.assertTrue(packingService.getFlower(flower1.getId(),null).getId().equals(flower1.getId()));
        Assert.assertTrue(packingService.readAllFlowers(null).size() == 1);
    }

    @Test
    public void getVegetable_ShouldWork(){
        Assert.assertTrue(packingService.getVegetable(vegetable1.getId(),null).getId().equals(vegetable1.getId()));
        Assert.assertTrue(packingService.readAllVegetables(null).size() == 1);
    }

    @Test
    public void readAllFlowers_ShouldWork(){
        Assert.assertTrue(packingService.readAllFlowers(null).size() == 2);
    }

    @Test
    public void readAllVegetables_ShouldWork(){
        Assert.assertTrue(packingService.readAllVegetables(null).size() == 2);
    }
}
