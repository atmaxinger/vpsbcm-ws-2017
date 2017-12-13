package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.ConfigServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.ContainerIPoint;

import java.util.Arrays;

public class SpaceServer {

    public static void main(String[] args) throws MzsCoreException {

        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);

        System.out.println("uri: " + core.getConfig().getSpaceUri());

        ConfigService configService = null;
        configService = new ConfigServiceImpl(core.getConfig().getSpaceUri());

        putInitialFlowerPlantCultivationInformation(configService);
        putInitialVegetablePlantCultivationInformation(configService);

        ContainerReference waterContainer = CapiUtil.lookupOrCreateContainer("waterContainer", core.getConfig().getSpaceUri(), Arrays.asList(new LabelCoordinator()), null, capi);
        capi.addContainerAspect(new WaterAspect(), waterContainer, ContainerIPoint.POST_TAKE);

        Water water = new Water();
        water.setAmount(250);
        capi.write(new Entry(water, LabelCoordinator.newCoordinationData(StorageServiceImpl.WATER_TOKEN_LABEL)), waterContainer);
    }

    public static void putInitialFlowerPlantCultivationInformation(ConfigService configService) {

        if (configService.readAllFlowerPlantCultivationInformation(null).size() == 0) {

            FlowerPlantCultivationInformation flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.ROSE);
            flowerPlantCultivationInformation.setSoilAmount(20);
            flowerPlantCultivationInformation.setWaterAmount(250);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, null);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.TULIP);
            flowerPlantCultivationInformation.setSoilAmount(15);
            flowerPlantCultivationInformation.setWaterAmount(375);
            flowerPlantCultivationInformation.setFertilizerAmount(2);
            flowerPlantCultivationInformation.setGrowthRate(0.5f);
            flowerPlantCultivationInformation.setHarvest(2);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, null);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.DAISY);
            flowerPlantCultivationInformation.setSoilAmount(23);
            flowerPlantCultivationInformation.setWaterAmount(237);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, null);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.VIOLET);
            flowerPlantCultivationInformation.setSoilAmount(27);
            flowerPlantCultivationInformation.setWaterAmount(250);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, null);
        }
    }

    public static void putInitialVegetablePlantCultivationInformation(ConfigService configService) {

        if (configService.readAllVegetablePlantCultivationInformation(null).size() == 0) {

            VegetablePlantCultivationInformation vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.PEPPER);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(2);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,null);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.TOMATO);
            vegetablePlantCultivationInformation.setSoilAmount(25);
            vegetablePlantCultivationInformation.setWaterAmount(600);
            vegetablePlantCultivationInformation.setFertilizerAmount(2);
            vegetablePlantCultivationInformation.setGrowthRate(0.35f);
            vegetablePlantCultivationInformation.setHarvest(3);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(3);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,null);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.CARROT);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(2);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,null);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.SALAD);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(1);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,null);
        }
    }
}
