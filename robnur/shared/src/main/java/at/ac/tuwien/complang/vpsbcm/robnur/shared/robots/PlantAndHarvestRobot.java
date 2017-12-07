package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlantAndHarvestRobot extends Robot {

    final static Logger logger = Logger.getLogger(PlantAndHarvestRobot.class);
    private StorageService storageService;
    private GreenhouseService greenhouseService;
    private TransactionService transactionService;
    private PackingService packingService;
    private CompostService compostService;

    private int plantTransactionTimeout = 60*1000;
    private int harvestTransactionTimeout = 1000;

    public PlantAndHarvestRobot(String id, int plantTransactionTimeout, int harvestTransactionTimeout, StorageService storageService, GreenhouseService greenhouseService, TransactionService transactionService, PackingService packingService, CompostService compostService) {
        this.setId(id);
        this.plantTransactionTimeout = plantTransactionTimeout;
        this.harvestTransactionTimeout = harvestTransactionTimeout;

        this.storageService = storageService;
        this.greenhouseService = greenhouseService;
        this.transactionService = transactionService;
        this.packingService = packingService;
        this.compostService = compostService;

        tryHarvestPlant();
        tryPlant();
    }

    /**
     * Try to harvest all harvestable plants
     */
    public void tryHarvestPlant() {
        tryHarvestFlower();
        tryHarvestVegetable();
    }

    /**
     * try to plant either a vegetable or a flower
     */
    public void tryPlant() {
        boolean hasPlantedSomething = false;

        // Step 1: get current planted plants from greenhouse
        List<VegetablePlant> vegetablePlants = greenhouseService.readAllVegetablePlants();
        List<FlowerPlant> flowerPlants = greenhouseService.readAllFlowerPlants();

        // Step 2: Check whether there are more flowers or vegetables currently planted
        if (vegetablePlants.size() < flowerPlants.size()) {
            // As there are more flowers planted, try to plant a vegetable
            hasPlantedSomething = tryPlantPlant(vegetablePlants, VegetableType.values());
            if (!hasPlantedSomething) {
                // If the vegetable could not be planted, try to plant a flower
                hasPlantedSomething = tryPlantPlant(flowerPlants, FlowerType.values());
            }
        } else if (vegetablePlants.size() > flowerPlants.size()) {
            hasPlantedSomething = tryPlantPlant(flowerPlants, FlowerType.values());
            if (!hasPlantedSomething) {
                hasPlantedSomething = tryPlantPlant(vegetablePlants, VegetableType.values());
            }
        } else {
            // There are equal amounts of flowers and vegetables planted. Flip a coin.
            Random random = new Random();
            if (random.nextBoolean()) {
                hasPlantedSomething = tryPlantPlant(vegetablePlants, VegetableType.values());
                if (!hasPlantedSomething) {
                    hasPlantedSomething = tryPlantPlant(flowerPlants, FlowerType.values());
                }
            } else {
                hasPlantedSomething = tryPlantPlant(flowerPlants, FlowerType.values());
                if (!hasPlantedSomething) {
                    hasPlantedSomething = tryPlantPlant(vegetablePlants, VegetableType.values());
                }
            }
        }

        // If something has been planted, try again to harvest and plant
        if(hasPlantedSomething) {
            tryHarvestPlant();
            tryPlant();
        }
    }


    private List<Vegetable> tryHarvestVegetablePlant(Transaction transaction) {
        VegetablePlant plant = greenhouseService.getHarvestableVegetablePlant(transaction);

        if(plant != null) {
            logger.info(String.format("harvested vegetables (%s) from plant %s", plant.getTypeName(), plant.getId()));

            List<Vegetable> vegetables = Vegetable.harvestVegetablesFormPlant(plant);

            // if this plant can still be harvested then "plant" it again
            if (plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
                greenhouseService.plant(plant, transaction);
            } else {
                plant.setCompostRobot(getId());
                compostService.putVegetablePlant(plant);
            }

            return vegetables;
        }

        return null;
    }

    /**
     * try to harvest all harvestable vegetables
     */
    private void tryHarvestVegetable() {
        logger.debug(String.format("PlantAndHarvestRobot %s: Try harvest vegetable", getId()));

        Transaction t = transactionService.beginTransaction(harvestTransactionTimeout);
        List<Vegetable> harvested = tryHarvestVegetablePlant(t);

        if (harvested != null && harvested.size() > 0) {

            for (Vegetable veg : harvested) {
                logger.info(String.format("PlantAndHarvestRobot %s: put vegetable(%s) into packing", getId(), veg.getId()));

                veg.setHarvestRobot(this.getId());
                packingService.putVegetable(veg);
            }

            t.commit();

            tryHarvestVegetable();
        } else {
            t.rollback();
        }
    }


    public List<Flower> tryHarvestFlowerPlant(Transaction transaction) {
        FlowerPlant plant = greenhouseService.getHarvestableFlowerPlant(transaction);

        if(plant != null) {
            logger.info(String.format("PlantAndHarvestRobot %s: harvested flowers(%s) from plant(%s)", getId(), plant.getTypeName(), plant.getId()));

            plant.setCompostRobot(getId());
            compostService.putFlowerPlant(plant);
            return Flower.harvestFlowerFromFlowerPlant(plant);
        }

        return null;
    }

    /**
     * try to harvest all harvestable flowers
     */
    private void tryHarvestFlower() {
        logger.debug(String.format("PlantAndHarvestRobot %s: Try harvest flower", getId()));

        Transaction t = transactionService.beginTransaction(harvestTransactionTimeout);
        List<Flower> harvested = tryHarvestFlowerPlant(t);
        if (harvested != null && harvested.size() > 0) {

            for (Flower flo : harvested) {
                logger.info(String.format("PlantAndHarvestRobot %s: put flower(%s) into packing", getId(), flo.getId()));

                flo.setHarvestRobot(this.getId());
                packingService.putFlower(flo);
            }

            t.commit();

            tryHarvestFlower();
        } else {
            t.rollback();
        }
    }

    /**
     * Find a new seed to plant (veg or flo)
     *
     * @param plantedPlants the plants (veg or flo) that have already been planted
     * @param types types (either VegetableType.values() or FlowerType.values())
     * @param t the transaction
     * @param <P> VegetablePlant or FlowerPlant
     * @param <E> VegetableType or FlowerType
     * @return a suitable seed or null
     */
    private <P extends Plant, E extends Enum<E>> P tryToGetNextSeed(List<P> plantedPlants, E[] types, Transaction t) {

        List<PlantCount<E>> counts = new LinkedList<>();

        // Step 1: count how many times a plant with the type has been planted
        int[] plantedTypes = new int[types.length];
        for (int i = 0; i < plantedTypes.length; i++) {
            plantedTypes[i] = 0;
        }

        for (P plant : plantedPlants) {
            int i = -1;
            // Find out ordinal by name
            for (int j = 0; j < types.length; j++) {
                if (plant.getTypeName().equals(types[j].name())) {
                    i = j;
                }
            }
            if (i == -1) {
                System.err.println("plant type not found");
                return null;
            }

            plantedTypes[i] = plantedTypes[i] + 1;
        }

        // Step 2: Create a sorted list with the types according to how many times the type has been planted
        for (int i = 0; i < plantedTypes.length; i++) {
            PlantCount<E> p = new PlantCount<>();
            p.plantType = types[i];
            p.count = plantedTypes[i];
            counts.add(p);
        }
        Collections.sort(counts);

        // Step 3: try to get the next plantable seed
        for (PlantCount<E> p : counts) {
            P plant = null;

            if (p.plantType instanceof VegetableType) {
                VegetablePlant tmp = storageService.tryGetSeed((VegetableType) p.plantType, t);
                plant = (P) tmp;
            } else if (p.plantType instanceof FlowerType) {
                FlowerPlant tmp = storageService.tryGetSeed((FlowerType) p.plantType, t);
                plant = (P) tmp;
            }

            if (plant != null) {
                logger.info(String.format("PlantAndHarvestRobot %s: Got a plant(%s, %s) to plant", getId(), plant.getTypeName(), plant.getId()));
                return (P) plant;
            }
        }

        return null;
    }

    /**
     * Trys to plant a new plant
     * @param planted the plants (veg or flo) that have already been planted
     * @param types types (either VegetableType.values() or FlowerType.values())
     * @param <P> VegetablePlant or FlowerPlant
     * @param <E> VegetableType or FlowerType
     * @return true if a new plant has been planted, false otherwise
     */
    private <P extends Plant, E extends Enum<E>> boolean tryPlantPlant(List<P> planted, E[] types) {
        Transaction t = transactionService.beginTransaction(plantTransactionTimeout);

        // Step 1: First of all, try to get a seed
        P nextSeed = tryToGetNextSeed(planted, types, t);
        if (nextSeed != null) {
            logger.info(String.format("PlantAndHarvestRobot %s: got %s seed", this.getId(), nextSeed.getTypeName()));

            // Step 2: try to get the amount of soil needed for the plant
            if (!storageService.tryGetExactAmountOfSoil(nextSeed.getCultivationInformation().getSoilAmount(), t)) {
                logger.info(String.format("PlantAndHarvestRobot %s: did not get exact amount of soil(%d) for seed(%s, %s)", this.getId(), nextSeed.getCultivationInformation().getSoilAmount(), nextSeed.getTypeName(), nextSeed.getId()));
                t.rollback();
                return false;
            }

            // Step 3: try to get the amount of fertilizer needed for the plant
            List fertilizers = null;
            if (nextSeed instanceof VegetablePlant) {
                fertilizers = storageService.getVegetableFertilizer(nextSeed.getCultivationInformation().getFertilizerAmount(), t);
            } else if (nextSeed instanceof FlowerPlant) {
                fertilizers = storageService.getFlowerFertilizer(nextSeed.getCultivationInformation().getFertilizerAmount(), t);
            }
            if (fertilizers == null || fertilizers.isEmpty()) {
                logger.info(String.format("PlantAndHarvestRobot %s: did not get enough fertilizer(%d) for seed(%s, %s)", this.getId(), nextSeed.getCultivationInformation().getFertilizerAmount(), nextSeed.getTypeName(), nextSeed.getId()));
                t.rollback();
                return false;
            }

            // Step 4: get the water needed for the plant
            storageService.getWater(nextSeed.getCultivationInformation().getWaterAmount());

            nextSeed.setPlantRobot(this.getId());

            // Step 5: plant the plant
            if (nextSeed instanceof VegetablePlant) {
                if(!greenhouseService.plant((VegetablePlant) nextSeed, t)) {
                    logger.info(String.format("PlantAndHarvestRobot %s: could not plant seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
                    t.rollback();
                    return false;
                }
                logger.debug(String.format("tryPlantPlant - planted vegetable %s", nextSeed.getTypeName()));
            } else if (nextSeed instanceof FlowerPlant) {
                if(!greenhouseService.plant((FlowerPlant) nextSeed, t)) {
                    logger.info(String.format("PlantAndHarvestRobot %s: could not plant seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
                    t.rollback();
                    return false;
                }
                logger.info(String.format("PlantAndHarvestRobot %s: planted seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
            } else {
                logger.log(Priority.toPriority(Priority.ERROR_INT), String.format("tryPlantPlant - not planted - unknown plant"));
                t.rollback();
                return false;
            }

            t.commit();
            return true;
        }

        // If we did not get any seed to plant, rollback the transaction
        logger.info(String.format("PlantAndHarvestRobot %s: did not get any seed", this.getId()));
        t.rollback();
        return false;
    }

    private class PlantCount<T> implements Comparable {
        T plantType;
        int count = 0;

        @Override
        public int compareTo(Object o) {
            return Integer.compare(count, ((PlantCount) o).count);
        }
    }







       /*private boolean tryPlantVegetable(List<VegetablePlant> plantedVegetables, Transaction t) {
        logger.debug("tryPlantVegetable");

        VegetablePlant nextSeed = tryToGetNextSeed(plantedVegetables, VegetableType.values(), t);
        if(nextSeed != null) {
            logger.debug(String.format("tryPlantVegetable - have nextSeed (%s)", nextSeed.getTypeName()));

            if(!storageService.tryGetExactAmountOfSoil(nextSeed.getCultivationInformation().getSoilAmount(), t)) {
                logger.debug(String.format("tryPlantVegetable - did not get exact amount of soil for %s - rollback", nextSeed.getTypeName()));

                t.rollback();
                return false;
            }

            logger.debug(String.format("tryPlantVegetable - have all soil for %s", nextSeed.getTypeName()));

            List<VegetableFertilizer> fertilizers = storageService.getVegetableFertilizer(nextSeed.getCultivationInformation().getFertilizerAmount(), t);
            if(fertilizers == null || fertilizers.isEmpty()) {
                logger.debug(String.format("tryPlantVegetable - did not get enough fertilizer for %s - rollback", nextSeed.getTypeName()));
                t.rollback();
                return false;
            }

            logger.debug(String.format("tryPlantVegetable - got all fertilizer for %s", nextSeed.getTypeName()));
            storageService.getWater(nextSeed.getCultivationInformation().getWaterAmount());
            logger.debug(String.format("tryPlantVegetable - got all water for %s", nextSeed.getTypeName()));


            greenhouseService.plant(nextSeed, t);

            logger.debug(String.format("tryPlantVegetable - planted %s", nextSeed.getTypeName()));

            t.commit();
            logger.debug(String.format("tryPlantVegetable - committed %s", nextSeed.getTypeName()));
            return true;
        }

        logger.debug(String.format("tryPlantVegetable - did not get any seed"));

        t.rollback();
        return false;
    }*/

    /*private VegetablePlant tryToGetNextVegetableSeed(List<VegetablePlant> plantedVegetables, Transaction t) {
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
    }*/

}
