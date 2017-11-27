package services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public abstract class MarketServiceTest{

    public MarketService marketService;

    Bouquet bouquet1, bouquet2;
    VegetableBasket vegetableBasket1, vegetableBasket2;


    public void init() {

        /* Bouquet */

        FlowerPlantCultivationInformation flowerPlantCultivationInformation1 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation1.setFlowerType(FlowerType.ROSE);

        FlowerPlantCultivationInformation flowerPlantCultivationInformation2 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation2.setFlowerType(FlowerType.TULIP);

        FlowerPlant flowerPlant1 = new FlowerPlant();
        flowerPlant1.setCultivationInformation(flowerPlantCultivationInformation1);

        FlowerPlant flowerPlant2 = new FlowerPlant();
        flowerPlant2.setCultivationInformation(flowerPlantCultivationInformation2);

        Flower flower1 = new Flower();
        flower1.setParentFlowerPlant(flowerPlant1);

        Flower flower2 = new Flower();
        flower2.setParentFlowerPlant(flowerPlant2);

        bouquet1 = new Bouquet();
        bouquet1.setFlowers(Arrays.asList(flower1,flower2));

        bouquet2 = new Bouquet();
        bouquet2.setFlowers(Arrays.asList(flower1));

        /* VegetableBasket */

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation1 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation1.setVegetableType(VegetableType.CARROT);

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation2 = new VegetablePlantCultivationInformation();
        vegetablePlantCultivationInformation2.setVegetableType(VegetableType.SALAD);

        VegetablePlant vegetablePlant1 = new VegetablePlant();
        vegetablePlant1.setCultivationInformation(vegetablePlantCultivationInformation1);

        VegetablePlant vegetablePlant2 = new VegetablePlant();
        vegetablePlant2.setCultivationInformation(vegetablePlantCultivationInformation2);

        Vegetable vegetable1 = new Vegetable();
        vegetable1.setParentVegetablePlant(vegetablePlant1);

        Vegetable vegetable2 = new Vegetable();
        vegetable2.setParentVegetablePlant(vegetablePlant2);

        vegetableBasket1 = new VegetableBasket();
        vegetableBasket1.setVegetables(Arrays.asList(vegetable1,vegetable2));

        vegetableBasket2 = new VegetableBasket();
        vegetableBasket2.setVegetables(Arrays.asList(vegetable1));

        /* Market */

        marketService.putBouquet(bouquet1);
        marketService.putBouquet(bouquet2);

        marketService.putVegetableBasket(vegetableBasket1);
        marketService.putVegetableBasket(vegetableBasket2);
    }

    @Test
    public void putBoutique_ShouldWork(){
        Assert.assertTrue(marketService.readAllBouquets().size() == 2);
        Assert.assertTrue(marketService.readAllBouquets().get(0).equals(bouquet1) || marketService.readAllBouquets().get(1).equals(bouquet2));
    }

    @Test
    public void getAmountOfBouquets_ShouldWork(){
        Assert.assertTrue(marketService.readAllBouquets().size() == 2);
    }

    @Test
    public void putVegetableBasket_ShouldWork(){
        Assert.assertTrue(marketService.readAllVegetableBaskets().size() == 2);
        Assert.assertTrue(marketService.readAllVegetableBaskets().get(0).equals(vegetableBasket1) || marketService.readAllVegetableBaskets().get(1).equals(vegetableBasket2));
    }

    @Test
    public void getAmountOfVegetableBaskets_ShouldWork(){
        Assert.assertTrue(marketService.readAllVegetableBaskets().size() == 2);
    }

    @Test
    public void readAllBouquets_ShouldWork(){
        Assert.assertTrue(marketService.readAllBouquets().size() == 2);
    }

    @Test
    public void sellBouquet_ShouldWork() {
        marketService.sellBouquet(bouquet2);
        Assert.assertTrue(marketService.readAllBouquets().size() == 1);
        Assert.assertTrue(marketService.readAllBouquets().get(0).equals(bouquet1));
    }

    @Test
    public void readAllVegetableBaskets_ShouldWork(){
        Assert.assertTrue(marketService.readAllVegetableBaskets().size() == 2);
    }

    @Test
    public void sellVegetableBasket_ShouldWork(){
        marketService.sellVegetableBasket(vegetableBasket2);
        Assert.assertTrue(marketService.readAllVegetableBaskets().size() == 1);
        Assert.assertTrue(marketService.readAllVegetableBaskets().get(0).equals(vegetableBasket1));
    }
}
