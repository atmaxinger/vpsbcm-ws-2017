package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import sun.security.krb5.Config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlantAndHarvestRobot extends Robot {

    final static Logger logger = Logger.getLogger(PlantAndHarvestRobot.class);


    private class PlantCount<T> implements Comparable {
        T plantType;
        int count=0;

        @Override
        public int compareTo(Object o) {
            return Integer.compare(count, ((PlantCount) o).count);
        }
    }

    private StorageService storageService;
    private GreenhouseService greenhouseService;
    private TransactionService transactionService;
    private PackingService packingService;

    public PlantAndHarvestRobot(StorageService storageService, GreenhouseService greenhouseService, TransactionService transactionService, PackingService packingService) {
        logger.debug("PlantAndHarvestRobot constructor");

        this.storageService = storageService;
        this.greenhouseService = greenhouseService;
        this.transactionService = transactionService;
        this.packingService = packingService;

        tryHarvestPlant("constructor");
        tryPlant();
    }


    /**
     *
     * @return true if something has been harvested
     */
    private void tryHarvestVegetable(String why) {
        logger.log(Priority.DEBUG, "tryHarvestVegetable - " + why);

        Transaction t = transactionService.beginTransaction(1000);
        List<Vegetable> harvested = greenhouseService.tryHarvestVegetablePlant(t);
        if(harvested != null) {
            for(Vegetable vegetable : harvested) {
                packingService.putVegetable(vegetable);
            }

            t.commit();
        }
        else {
            t.rollback();
        }

        if(harvested != null && harvested.size() > 0) {
            for(Vegetable veg : harvested) {
                veg.setHarvestRobot(this.getId());
                packingService.putVegetable(veg);
            }

            tryHarvestVegetable("recursion");
        }
    }

    /**
     *
     * @return true if something has been harvested
     */
    private void tryHarvestFlower() {
        logger.log(Priority.DEBUG, "tryHarvestFlower");

        Transaction t = transactionService.beginTransaction(1000);
        List<Flower> harvested = greenhouseService.tryHarvestFlowerPlant(t);
        if(harvested != null) {
            for(Flower flower : harvested) {
                packingService.putFlower(flower);
            }

            t.commit();
        }
        else {
            t.rollback();
        }

        if(harvested != null && harvested.size() > 0) {
            for(Flower flo : harvested) {
                flo.setHarvestRobot(this.getId());
                packingService.putFlower(flo);
            }

            tryHarvestFlower();
        }
    }

    public void tryHarvestPlant(String why) {
        logger.debug("tryHarvestPlant - " + why);

        tryHarvestFlower();
        tryHarvestVegetable("harvestPlant - " + why);
    }

    public void tryPlant() {
        logger.debug("tryPlant");
        Transaction t = transactionService.beginTransaction(60*1000);

        List<VegetablePlant> vegetablePlants = greenhouseService.readAllVegetablePlants(t);
        /*List<FlowerPlant> flowerPlants = greenhouseService.readAllFlowerPlants(t);

        if(vegetablePlants.size() < flowerPlants.size()) {
            tryPlantVegetable(vegetablePlants);
        }
        else if(vegetablePlants.size() > flowerPlants.size()) {
            tryPlantFlower(flowerPlants);
        }
        else {
            Random random = new Random();
            if(random.nextBoolean()) {
                tryPlantVegetable(vegetablePlants, t);
            } else {
                tryPlantFlower(flowerPlants, t);
            }
        }*/

        tryPlantVegetable(vegetablePlants, t);
    }


    private boolean tryPlantVegetable(List<VegetablePlant> plantedVegetables, Transaction t) {
        logger.log(Priority.toPriority(Priority.DEBUG_INT), "tryPlantVegetable");

        VegetablePlant nextSeed = tryToGetNextVegetableSeed(plantedVegetables, t);
        if(nextSeed != null) {
            logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - have nextSeed (%s)", nextSeed.getTypeName()));

            if(!storageService.tryGetExactAmountOfSoil(nextSeed.getCultivationInformation().getSoilAmount(), t)) {
                logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - did not get exact amount of soil for %s - rollback", nextSeed.getTypeName()));

                t.rollback();
                return false;
            }

            logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - have all soil for %s", nextSeed.getTypeName()));

            List<VegetableFertilizer> fertilizers = storageService.getVegetableFertilizer(nextSeed.getCultivationInformation().getFertilizerAmount(), t);
            if(fertilizers == null || fertilizers.isEmpty()) {
                logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - did not get enough fertilizer for %s - rollback", nextSeed.getTypeName()));
                t.rollback();
                return false;
            }

            logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - got all fertilizer for %s", nextSeed.getTypeName()));
            storageService.getWater(nextSeed.getCultivationInformation().getWaterAmount());
            logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - got all water for %s", nextSeed.getTypeName()));


            greenhouseService.plant(nextSeed, t);

            logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - planted %s", nextSeed.getTypeName()));

            t.commit();
            logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - committed %s", nextSeed.getTypeName()));
            return true;
        }

        logger.log(Priority.toPriority(Priority.DEBUG_INT), String.format("tryPlantVegetable - did not get any seed"));

        t.rollback();
        return false;
    }

    private VegetablePlant tryToGetNextVegetableSeed(List<VegetablePlant> plantedVegetables, Transaction t) {
        logger.log(Priority.DEBUG, "tryToGetNextVegetableSeed");

        List<PlantCount<VegetableType>> counts = new LinkedList<>();

        // Step 1: count how many times a plant with the type has been planted
        int[] plantedTypes = new int[VegetableType.values().length];
        for(int i=0; i<plantedTypes.length; i++) {
            plantedTypes[i]=0;
        }

        for(VegetablePlant plant : plantedVegetables) {
            int i = plant.getCultivationInformation().getVegetableType().ordinal();
            plantedTypes[i] = plantedTypes[i]+1;
        }

        // Step 2: Create a sorted list with the types according to how many times the type has been planted
        for(int i=0; i<plantedTypes.length; i++) {
            PlantCount<VegetableType> p = new PlantCount<>();
            p.plantType = VegetableType.values()[i];
            p.count = plantedTypes[i];
            counts.add(p);
        }
        Collections.sort(counts);

        // Step 3: try to get the next plantable seed
        for(PlantCount<VegetableType> p : counts) {
            VegetablePlant plant = storageService.tryGetSeed(p.plantType, t);
            if(plant != null) {
                return plant;
            }
        }

        return null;
    }



    /*private void tryPlantFlower(List<FlowerPlant> plantedFlowers, Transaction t) {

    }*/

   /* private FlowerType nextFlower(List<FlowerPlant> plantedFlowers) {
        int[] plantedTypes = new int[FlowerType.values().length];
        for(int i=0; i<plantedTypes.length; i++) {
            plantedTypes[i]=0;
        }

        for(FlowerPlant plant : plantedFlowers) {
            int i = plant.getCultivationInformation().getFlowerType().ordinal();
            plantedTypes[i] = plantedTypes[i]+1;
        }


        int leastPlanted=-1;
        int leastPlantedCount = Integer.MAX_VALUE;
        for(int i=0; i<plantedTypes.length; i++) {
            if(plantedTypes[i] < leastPlantedCount) {
                leastPlanted = i;
                leastPlantedCount = plantedTypes[i];
            }
        }

        return FlowerType.values()[leastPlanted];
    }*/
}
