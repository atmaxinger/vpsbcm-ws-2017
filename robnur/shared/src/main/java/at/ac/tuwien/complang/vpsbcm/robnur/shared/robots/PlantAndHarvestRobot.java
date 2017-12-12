package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.*;

public class PlantAndHarvestRobot extends Robot {

    private HashMap<String,Integer> plantCount = new HashMap<>();

    final static Logger logger = Logger.getLogger(PlantAndHarvestRobot.class);
    private StorageService storageService;
    private GreenhouseService greenhouseService;
    private TransactionService transactionService;
    private PackingService packingService;
    private CompostService compostService;

    private int plantTransactionTimeout = -1; // 60*1000;
    private int harvestTransactionTimeout = -1; //1000;

    private List<String> plantedVegetablePlantIds = new LinkedList<>();
    private List<String> harvestedVegetablePlantIds = new LinkedList<>();

    private int plantedVegetablePlants = 0;
    private int harvestedVegetablePlants = 0;
    private int harvestedVegetables = 0;

    private int plantedFlowerPlants = 0;
    private int harvestedFlowerPlants = 0;
    private int harvestedFlowers = 0;

    private void outputStatistics() {
        //System.out.println(String.format("----- PLANTED VEGETABLE PLANTS: %d | HARVESTED VEGETABLE PLANTS: %d | HARVESTED VEGETABLES: %d -----", plantedVegetablePlants, harvestedVegetablePlants, harvestedVegetables));
        ///System.out.println(String.format("-------- MISSING VEGETABLE PLANT IDS: %s ----" , formatList(getNotHarvestedIds(plantedVegetablePlantIds, harvestedVegetablePlantIds))));
        //System.out.println(String.format("----- PLANTED FLOWER PLANTS: %d | HARVESTED FLOWER PLANTS: %d | HARVESTED FLOWERS: %d -----", plantedFlowerPlants, harvestedFlowerPlants, harvestedFlowers));
    }

    private String formatList(List<String> list) {
        String s = "";

        for(int i=0; i<list.size(); i++) {
            s += list.get(i);
            if(i < list.size()-1) {
                s+=", ";
            }
        }

        return s;
    }

    private List<String> getNotHarvestedIds(List<String> plantedIds, List<String> harvestedIds) {
        List<String> missing = new LinkedList<>();

        for (String planted : plantedIds) {
            if(!harvestedIds.contains(planted)) {
                missing.add(planted);
            }
        }

        return missing;
    }

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
    public synchronized void tryHarvestPlant() {
        tryHarvestFlower();
        tryHarvestVegetable();

        outputStatistics();
    }

    /**
     * try to plant either a vegetable or a flower
     */
    public synchronized void tryPlant() {

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

        outputStatistics();
    }


    private List<Vegetable> tryHarvestVegetablePlant(Transaction transaction) {

        VegetablePlant plant = greenhouseService.getHarvestableVegetablePlant(transaction);

        if(plant != null) {

            int before = plant.getCultivationInformation().getRemainingNumberOfHarvests();

            List<Vegetable> vegetables = Vegetable.harvestVegetablesFormPlant(plant);
            logger.info(String.format("harvested vegetables (%s) from plant %s", plant.getTypeName(), plant.getId()));

            // if this plant can still be harvested then "plant" it again
            if (plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
                if(!greenhouseService.plant(plant, transaction)) {
                    logger.error("could not put vegetable plant with still remaining harvests back - return null");
                    return null;
                }

                // 2017-12-09 19:32:39 ERROR PlantAndHarvestRobot - cnt + plant.getCultivationInformation().getRemainingNumberOfHarvests() != 3;  3 + 1 != 3; before was: 2; plantId: 9a54918c-eb4a-411f-b3d2-802726849d9f
                // 2017-12-09 19:32:34 ERROR PlantAndHarvestRobot - cnt + plant.getCultivationInformation().getRemainingNumberOfHarvests() != 3;  2 + 2 != 3; before was: 3; plantId: 9a54918c-eb4a-411f-b3d2-802726849d9f

                /*if(plantCount.size() != 0) {
                    int cnt = plantCount.get(plant.getId());
                    if (cnt + plant.getCultivationInformation().getRemainingNumberOfHarvests() != 3) {
                        logger.error(String.format("cnt + plant.getCultivationInformation().getRemainingNumberOfHarvests() != 3;  %d + %d != 3; before was: %d; plantId: %s, threadid: %s, growth: %d", cnt, plant.getCultivationInformation().getRemainingNumberOfHarvests(), before, plant.getId(), Thread.currentThread().getId(), plant.getGrowth()));
                    }
                    plantCount.put(plant.getId(), cnt + 1);
                }
                {
                    logger.error(String.format("plant with id %s has not been planted with this robot.", plant.getId()));
                }*/

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
        logger.debug(String.format("PlantAndHarvestRobot %s: Try harvest vegetable", getId()));

        Transaction t = transactionService.beginTransaction(harvestTransactionTimeout, "Harvest Vegetable - " + harvestedVegetablePlants);
        List<Vegetable> harvested = tryHarvestVegetablePlant(t);

        if (harvested != null && harvested.size() > 0) {

            for (Vegetable veg : harvested) {
                logger.info(String.format("PlantAndHarvestRobot %s: put vegetable(%s) into packing, threadid = %s", getId(), veg.getId(),Thread.currentThread().getId()));

                veg.setHarvestRobot(this.getId());
                packingService.putVegetable(veg,t);
            }

            t.commit();

            harvestedVegetablePlants++;
            harvestedVegetables += harvested.size();
            harvestedVegetablePlantIds.add(harvested.get(0).getParentPlant().getId());

            tryHarvestVegetable();
        } else {
            t.rollback();
            logger.info("tryHarvestVegetable ------------- Rollback");
        }
    }


    private List<Flower> tryHarvestFlowerPlant(Transaction transaction) {
        FlowerPlant plant = greenhouseService.getHarvestableFlowerPlant(transaction);

        if(plant != null) {
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
        logger.debug(String.format("PlantAndHarvestRobot %s: Try harvest flower", getId()));

        Transaction t = transactionService.beginTransaction(harvestTransactionTimeout, "Harvest Flower");
        List<Flower> harvested = tryHarvestFlowerPlant(t);
        if (harvested != null && harvested.size() > 0) {

            for (Flower flo : harvested) {
                logger.info(String.format("PlantAndHarvestRobot %s: put flower(%s) into packing", getId(), flo.getId()));

                flo.setHarvestRobot(this.getId());
                packingService.putFlower(flo, t);
            }

            t.commit();

            harvestedFlowers+=harvested.size();
            harvestedFlowerPlants++;

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

        logger.info("try To Get Next Seed");

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
                //logger.info(String.format("PlantAndHarvestRobot %s: Got a plant(%s, %s) to plant", getId(), plant.getTypeName(), plant.getId()));
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
        Transaction transaction = transactionService.beginTransaction(plantTransactionTimeout, "Plant Plant");

        // Step 1: First of all, try to get a seed
        P nextSeed = tryToGetNextSeed(planted, types, transaction);
        if (nextSeed != null) {
            logger.info(String.format("PlantAndHarvestRobot %s: got %s seed", this.getId(), nextSeed.getTypeName()));

            // Step 2: try to get the amount of soil needed for the plant
            if (!storageService.tryGetExactAmountOfSoil(nextSeed.getCultivationInformation().getSoilAmount(), transaction)) {
                logger.info(String.format("PlantAndHarvestRobot %s: did not get exact amount of soil(%d) for seed(%s, %s)", this.getId(), nextSeed.getCultivationInformation().getSoilAmount(), nextSeed.getTypeName(), nextSeed.getId()));
                transaction.rollback();printallSeeds(nextSeed,null);
                return false;
            }

            // Step 3: try to get the amount of fertilizer needed for the plant
            List fertilizers = null;
            if (nextSeed instanceof VegetablePlant) {
                fertilizers = storageService.getVegetableFertilizer(nextSeed.getCultivationInformation().getFertilizerAmount(), transaction);
            } else if (nextSeed instanceof FlowerPlant) {
                fertilizers = storageService.getFlowerFertilizer(nextSeed.getCultivationInformation().getFertilizerAmount(), transaction);
            }
            if (fertilizers == null || fertilizers.isEmpty()) {
                logger.info(String.format("PlantAndHarvestRobot %s: did not get enough fertilizer(%d) for seed(%s, %s)", this.getId(), nextSeed.getCultivationInformation().getFertilizerAmount(), nextSeed.getTypeName(), nextSeed.getId()));
                transaction.rollback();
                printallSeeds(nextSeed,null);
                return false;
            }

            logger.info("about to access water");
            // Step 4: get the water needed for the plant
            storageService.getWater(nextSeed.getCultivationInformation().getWaterAmount());

            nextSeed.setPlantRobot(this.getId());

            // Step 5: plant the plant
            if (nextSeed instanceof VegetablePlant) {
                if(!greenhouseService.plant((VegetablePlant) nextSeed, transaction)) {
                    logger.info(String.format("PlantAndHarvestRobot %s: could not plant seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
                    transaction.rollback();
                    printallSeeds(nextSeed,null);
                    return false;
                }
                logger.debug(String.format("tryPlantPlant - planted vegetable %s", nextSeed.getTypeName()));
            } else if (nextSeed instanceof FlowerPlant) {
                if(!greenhouseService.plant((FlowerPlant) nextSeed, transaction)) {
                    logger.info(String.format("PlantAndHarvestRobot %s: could not plant seed(%s, %s)", this.getId(), nextSeed.getTypeName(), nextSeed.getId()));
                    transaction.rollback();
                    printallSeeds(nextSeed,null);
                    return false;
                }

            } else {
                logger.error(String.format("tryPlantPlant - not planted - unknown plant"));
                transaction.rollback();
                printallSeeds(nextSeed,null);
                return false;
            }


            transaction.commit();
            logger.debug(String.format("-- planted plant %s %s", nextSeed.getId(), nextSeed.getTypeName()));

            if(plantCount.containsKey(nextSeed.getId())){
                logger.error("Again planted seed " + nextSeed.getId());
            }
            plantCount.put(nextSeed.getId(),1);

            if (nextSeed instanceof VegetablePlant) {
                plantedVegetablePlants++;
                plantedVegetablePlantIds.add(nextSeed.getId());
            } else if (nextSeed instanceof FlowerPlant) {
                plantedFlowerPlants++;
            }

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

    private synchronized void printallSeeds(Plant plant, Transaction transaction) {
        if(plant instanceof FlowerPlant) {
            FlowerType type = ((FlowerPlant) plant).cultivationInformation.getFlowerType();
            List<FlowerPlant> flowerSeeds = storageService.getSeeds(type, transaction);
            storageService.putFlowerSeeds(flowerSeeds, transaction);

            String s = "";
            for (FlowerPlant seed : flowerSeeds) {
                s += String.format("%s(%s),", seed.getTypeName(), seed.getId());
            }
            logger.debug(String.format("AVAILABLE FLOWER SEEDS (%d): %s", flowerSeeds.size(), s));

            boolean containsType = false;

            for (FlowerPlant seed : flowerSeeds) {
                if (seed.getTypeName().contains(plant.getTypeName())) {
                    containsType = true;
                    break;
                }
            }

            if (!containsType) {
                logger.fatal("NO FLOWERS OF SAME TYPE FOUND");
            }
        }
    }
}
