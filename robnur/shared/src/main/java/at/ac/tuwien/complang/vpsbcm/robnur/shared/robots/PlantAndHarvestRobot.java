package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import org.apache.log4j.Logger;

import java.util.*;

public class PlantAndHarvestRobot extends Robot {

    final static Logger logger = Logger.getLogger(PlantAndHarvestRobot.class);
    private StorageService storageService;
    private GreenhouseService greenhouseService;
    private TransactionService transactionService;
    private PackingService packingService;
    private CompostService compostService;

    private int plantTransactionTimeout = -1;
    private int harvestTransactionTimeout = -1;

    public PlantAndHarvestRobot(String id, int plantTransactionTimeout, int harvestTransactionTimeout, StorageService storageService, GreenhouseService greenhouseService, TransactionService transactionService, PackingService packingService, CompostService compostService) {
        this.setId(id);
        this.plantTransactionTimeout = plantTransactionTimeout;
        this.harvestTransactionTimeout = harvestTransactionTimeout;

        this.storageService = storageService;
        this.greenhouseService = greenhouseService;
        this.transactionService = transactionService;
        this.packingService = packingService;
        this.compostService = compostService;

        doStuff();
    }


    public synchronized void doStuff() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        tryThrowAwayPlants();
        tryHarvestPlant();
        tryPlant();
    }

    private synchronized void tryThrowAwayPlants() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        logger.debug("tryThrowAwayPlants()");

        tryThrowAwayFlowers();
        tryThrowAwayVegetables();
    }

    /**
     * Try to harvest all harvestable plants
     */
    private synchronized void tryHarvestPlant() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        logger.debug("tryHarvestPlant()");


        tryHarvestFlower();
        tryHarvestVegetable();
    }

    /**
     * try to plant either a vegetable or a flower
     */
    private synchronized void tryPlant() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        logger.debug("tryPlant()");

        boolean hasPlantedSomething = false;

        // Step 1: get current planted plants from greenhouse
        //         and combine them with the already composted plants
        List<VegetablePlant> vegetablePlants = greenhouseService.readAllVegetablePlants();
        vegetablePlants.addAll(compostService.readAllVegetablePlants());

        List<FlowerPlant> flowerPlants = greenhouseService.readAllFlowerPlants();
        flowerPlants.addAll(compostService.readAllFlowerPlants());

        // Step 2: Check whether there are more flowers or vegetables currently planted
        if (vegetablePlants.size() < flowerPlants.size()) {
            // As there are more flowers planted, try to plant a vegetable
            hasPlantedSomething = tryPlantPlant(vegetablePlants, VegetableType.values());
            if (storageService.isExit()) {
                return;
            }
            if (!hasPlantedSomething) {
                // If the vegetable could not be planted, try to plant a flower
                hasPlantedSomething = tryPlantPlant(flowerPlants, FlowerType.values());
            }
        } else if (vegetablePlants.size() > flowerPlants.size()) {
            hasPlantedSomething = tryPlantPlant(flowerPlants, FlowerType.values());
            if (storageService.isExit()) {
                return;
            }
            if (!hasPlantedSomething) {
                hasPlantedSomething = tryPlantPlant(vegetablePlants, VegetableType.values());
            }
        } else {
            // There are equal amounts of flowers and vegetables planted. Flip a coin.

            if (storageService.isExit()) {
                return;
            }
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

        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        // If something has been planted, try again to harvest and plant
        if (hasPlantedSomething) {
            tryHarvestPlant();
            tryPlant();
        }
    }


    private void tryThrowAwayFlowers() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        Transaction transaction = transactionService.beginTransaction(-1);

        FlowerPlant plant = greenhouseService.getLimpFlowerPlant(transaction);
        if(plant != null) {
            logger.info(String.format("throwing away limp flower plant %s (%s)", plant.getTypeName(), plant.getId()));
            plant.setCompostRobot(getId());
            compostService.putFlowerPlant(plant, transaction);
            transaction.commit();

            tryThrowAwayFlowers();
        }
        else {
            transaction.rollback();
        }
    }

    private void tryThrowAwayVegetables() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        Transaction transaction = transactionService.beginTransaction(-1);

        VegetablePlant plant = greenhouseService.getLimpVegetablePlant(transaction);
        if(plant != null) {
            logger.info(String.format("throwing away limp vegetable plant %s (%s)", plant.getTypeName(), plant.getId()));
            plant.setCompostRobot(getId());
            compostService.putVegetablePlant(plant, transaction);
            transaction.commit();

            tryThrowAwayVegetables();
        }
        else {
            transaction.rollback();
        }
    }

    private List<Vegetable> tryHarvestVegetablePlant(Transaction transaction) {

        VegetablePlant plant = greenhouseService.getHarvestableVegetablePlant(transaction);

        if (plant != null) {
            List<Vegetable> vegetables = Vegetable.harvestVegetablesFormPlant(plant);
            logger.info(String.format("harvested vegetables (%s) from plant %s", plant.getTypeName(), plant.getId()));

            // if this plant can still be harvested then "plant" it again
            if (plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
                if (!greenhouseService.plant(plant, transaction)) {
                    return null;
                }

            } else {
                plant.setCompostRobot(getId());
                compostService.putVegetablePlant(plant, transaction);
            }

            return vegetables;
        }

        return null;
    }

    /**
     * try to harvest all harvestable vegetables
     */
    public void tryHarvestVegetable() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        Transaction t = transactionService.beginTransaction(harvestTransactionTimeout, "Harvest Vegetable");
        List<Vegetable> harvested = tryHarvestVegetablePlant(t);

        if (harvested != null && harvested.size() > 0) {

            for (Vegetable veg : harvested) {
                logger.info(String.format("PlantAndHarvestRobot %s: put vegetable(%s) into packing, threadid = %s", getId(), veg.getId(), Thread.currentThread().getId()));

                veg.setHarvestRobot(this.getId());
                packingService.putVegetable(veg, t);
            }

            t.commit();

            tryHarvestVegetable();
        } else {
            t.rollback();
        }
    }


    private List<Flower> tryHarvestFlowerPlant(Transaction transaction) {
        FlowerPlant plant = greenhouseService.getHarvestableFlowerPlant(transaction);

        if (plant != null) {
            logger.info(String.format("PlantAndHarvestRobot %s: harvested flowers(%s) from plant(%s)", getId(), plant.getTypeName(), plant.getId()));

            plant.setCompostRobot(getId());
            compostService.putFlowerPlant(plant, transaction);
            return Flower.harvestFlowerFromFlowerPlant(plant);
        }

        return null;
    }

    /**
     * try to harvest all harvestable flowers
     */
    public void tryHarvestFlower() {
        if (storageService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        Transaction t = transactionService.beginTransaction(harvestTransactionTimeout, "Harvest Flower");
        List<Flower> harvested = tryHarvestFlowerPlant(t);
        if (harvested != null && harvested.size() > 0) {

            for (Flower flo : harvested) {
                logger.info(String.format("PlantAndHarvestRobot %s: put flower(%s) into packing", getId(), flo.getId()));

                flo.setHarvestRobot(this.getId());
                packingService.putFlower(flo, t);
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
     * @param types         types (either VegetableType.values() or FlowerType.values())
     * @param t             the transaction
     * @param <P>           VegetablePlant or FlowerPlant
     * @param <E>           VegetableType or FlowerType
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
                return (P) plant;
            }
        }

        return null;
    }

    /**
     * Trys to plant a new plant
     *
     * @param planted the plants (veg or flo) that have already been planted
     * @param types   types (either VegetableType.values() or FlowerType.values())
     * @param <P>     VegetablePlant or FlowerPlant
     * @param <E>     VegetableType or FlowerType
     * @return true if a new plant has been planted, false otherwise
     */
    private <P extends Plant, E extends Enum<E>> boolean tryPlantPlant(List<P> planted, E[] types) {
        if (storageService.isExit()) {
            return false;
        }

        Transaction transaction = transactionService.beginTransaction(plantTransactionTimeout, "Plant Plant");

        // Step 1: First of all, try to get a seed
        P nextSeed = tryToGetNextSeed(planted, types, transaction);
        if (nextSeed != null) {
            logger.info(String.format("PlantAndHarvestRobot %s: got %s seed", this.getId(), nextSeed.getTypeName()));

            // Step 2: get the water needed for the plant
            storageService.getWater(nextSeed.getCultivationInformation().getWaterAmount(), this.getId());

            nextSeed.setPlantRobot(this.getId());

            // Step 3: plant the plant
            if (nextSeed instanceof VegetablePlant) {
                if (!greenhouseService.plant((VegetablePlant) nextSeed, transaction)) {
                    logger.info(String.format("PlantAndHarvestRobot %s: could not plant seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
                    transaction.rollback();
                    return false;
                }
            } else if (nextSeed instanceof FlowerPlant) {
                if (!greenhouseService.plant((FlowerPlant) nextSeed, transaction)) {
                    logger.info(String.format("PlantAndHarvestRobot %s: could not plant seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
                    transaction.rollback();
                    return false;
                }

            } else {
                transaction.rollback();
                return false;
            }


            transaction.commit();
            logger.debug(String.format("PlantAndHarvestRobot %s: planted %s plant %s", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
            return true;
        }

        // If we did not get any seed to plant, rollback the transaction
        logger.info(String.format("PlantAndHarvestRobot %s: did not get any seed", this.getId()));
        transaction.rollback();
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
}
