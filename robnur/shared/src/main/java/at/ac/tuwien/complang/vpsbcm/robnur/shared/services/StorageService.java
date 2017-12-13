package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class StorageService {

    public interface Callback<T> {
        void handle(T data);
    }

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(StorageService.class);
    
    private Callback<List<FlowerPlant>> flowerSeedsChanged;
    private Callback<List<VegetablePlant>> vegetableSeedChanged;
    private Callback<List<SoilPackage>> soilPackagesChanged;
    private Callback<List<FlowerFertilizer>> flowerFertilizerChanged;
    private Callback<List<VegetableFertilizer>> vegetableFertilizerChanged;

    protected void notifyFlowerSeedsChanged(List<FlowerPlant> list) {
        if(flowerSeedsChanged != null) {
            logger.debug("notify FlowerSeeds Changed");
            flowerSeedsChanged.handle(list);
        }
    }

    protected void notifyVegetableSeedsChanged(List<VegetablePlant> list) {
        if(vegetableSeedChanged != null) {
            vegetableSeedChanged.handle(list);
        }
    }

    protected void notifySoilPackagesChanged(List<SoilPackage> list) {
        if(soilPackagesChanged != null) {
            logger.debug("notify SoilPackages Changed");
            soilPackagesChanged.handle(list);
        }
    }

    protected void notifyFlowerFertilizerChanged(List<FlowerFertilizer> list) {
        if(flowerFertilizerChanged != null) {
            logger.debug("notify FlowerFertilizer Changed");
            flowerFertilizerChanged.handle(list);
        }
    }

    protected void notifyVegetableFertilizerChanged(List<VegetableFertilizer> list) {
        if(vegetableFertilizerChanged != null) {
            vegetableFertilizerChanged.handle(list);
        }
    }

    public void onFlowerSeedChanged(Callback<List<FlowerPlant>> seedsChanged) {
        this.flowerSeedsChanged = seedsChanged;
    }

    public void onVegetableSeedsChanged(Callback<List<VegetablePlant>> seedsChanged) {
        this.vegetableSeedChanged = seedsChanged;
    }

    public void onSoilPackagesChanged(Callback<List<SoilPackage>> soilPackagesChanged) {
        this.soilPackagesChanged = soilPackagesChanged;
    }

    public void onFlowerFertilizerChanged(Callback<List<FlowerFertilizer>> flowerFertilizerChanged) {
        this.flowerFertilizerChanged = flowerFertilizerChanged;
    }

    public void onVegetableFertilizerChanged(Callback<List<VegetableFertilizer>> vegetableFertilizerChanged) {
        this.vegetableFertilizerChanged = vegetableFertilizerChanged;
    }

    /**
     * Trys to get a seed of the specified type that can be planted.
     * This means that there is enough fertilizer and enough soil available.
     * Automatically takes the amount of fertilizer and soil from the storage.
     *
     * @param type The type of the seed
     * @param transaction the transaction
     * @return seed that can be planted or null
     */
    public VegetablePlant tryGetSeed(VegetableType type, Transaction transaction) {
        VegetablePlant plant = getSeed(type, transaction);

        if(plant == null) {
            logger.debug(String.format("did not get any seed for vegetable %s", type));
            return null;
        }

        List<VegetableFertilizer> fertilizers = getVegetableFertilizer(plant.getCultivationInformation().getFertilizerAmount(), transaction);
        if(fertilizers == null) {
            putVegetableSeeds(Collections.singletonList(plant), transaction);
            logger.debug(String.format("did not get enough vegetable fertilizer (%d) for %s", plant.getCultivationInformation().getFertilizerAmount(), type));
            return null;
        }

        if(!tryGetExactAmountOfSoil(plant.getCultivationInformation().getSoilAmount(), transaction)) {
            putVegetableSeeds(Collections.singletonList(plant), transaction);
            putVegetableFertilizers(fertilizers, transaction);
            logger.debug(String.format("did not get enough soil (%d) for %s", plant.getCultivationInformation().getSoilAmount(), type));
            return null;
        }

        return plant;
    }


    /**
     * Trys to get a seed of the specified type that can be planted.
     * This means that there is enough fertilizer and enough soil available.
     * Automatically takes the amount of fertilizer and soil from the storage.
     *
     * @param type The type of the seed
     * @param transaction the transaction
     * @return seed that can be planted or null
     */
    public FlowerPlant tryGetSeed(FlowerType type, Transaction transaction) {
        FlowerPlant plant = getSeed(type, transaction);

        if(plant == null) {
            logger.debug(String.format("did not get any seed for flower %s", type));
            return null;
        }

        List<FlowerFertilizer> fertilizers = getFlowerFertilizer(plant.getCultivationInformation().getFertilizerAmount(), transaction);
        if(fertilizers == null) {
            putFlowerSeeds(Collections.singletonList(plant), transaction);
            logger.debug(String.format("did not get enough flower fertilizer (%d) for %s", plant.getCultivationInformation().getFertilizerAmount(), type));
            return null;
        }

        if(!tryGetExactAmountOfSoil(plant.getCultivationInformation().getSoilAmount(), transaction)) {
            putFlowerSeeds(Collections.singletonList(plant), transaction);
            putFlowerFertilizers(fertilizers, transaction);
            logger.debug(String.format("did not get enough soil (%d) for %s", plant.getCultivationInformation().getSoilAmount(), type));
            return null;
        }

        return plant;
    }

    /**
     * Get a flower seed with the specified flower type
     *
     * @param type type of flower
     * @param transaction the transaction
     * @return seed of flower if successful, null otherwise
     */
    protected abstract FlowerPlant getSeed(FlowerType type, Transaction transaction);
    /**
     * Get a vegetable seed with the specified vegetable type
     *
     * @param type type of vegetable
     * @param transaction the transaction
     * @return seed of vegetable if successful, null otherwise
     */
    protected abstract VegetablePlant getSeed(VegetableType type, Transaction transaction);

    /**
     * Put the seed into the storage
     *
     * @param plant the seed
     * @param transaction the transaction
     */
    public abstract void putSeed(FlowerPlant plant, Transaction transaction);
    /**
     * Put the seed into the storage
     *
     * @param plant the seed
     * @param transaction the transaction
     */
    public abstract void putSeed(VegetablePlant plant, Transaction transaction);

    /**
     * Put a list of seeds into the storage
     *
     * @param plants the seeds
     * @param transaction the transaction
     */
    public abstract void putFlowerSeeds(List<FlowerPlant> plants, Transaction transaction);
    /**
     * Put a list of seeds into the storage
     *
     * @param plants the seeds
     * @param transaction the transaction
     */
    public abstract void putVegetableSeeds(List<VegetablePlant> plants, Transaction transaction);


    /**
     * Read all currently available flower seeds (without setting a lock)
     * @return list of all currently available flower seeds if successful, or null otherwise
     */
    public List<FlowerPlant> readAllFlowerSeeds() {
        return readAllFlowerSeeds(null);
    }
    /**
     * Read all currently available flower seeds
     * @param transaction the transaction
     * @return list of all currently available flower seeds if successful, or null otherwise
     */
    public abstract List<FlowerPlant> readAllFlowerSeeds(Transaction transaction);

    /**
     * Read all currently available vegetable seeds (without setting a lock)
     * @return list of all currently available vegetable seeds if successful, or null otherwise
     */
    public List<VegetablePlant> readAllVegetableSeeds() {
        return readAllVegetableSeeds(null);
    }
    /**
     * Read all currently available vegetable seeds
     * @param transaction the transaction
     * @return list of all currently available vegetable seeds if successful, or null otherwise
     */
    public abstract List<VegetablePlant> readAllVegetableSeeds(Transaction transaction);

    /**
     * Take the amount of flower fertilizers from the storage (read + delete)
     * @param amount the amount to get
     * @param transaction the transaction
     * @return list of flower fertilizers if successful, or null otherwise
     */
    public abstract List<FlowerFertilizer> getFlowerFertilizer(int amount, Transaction transaction);

    /**
     * Put one flower fertilizer into the storage
     * @param flowerFertilizer the fertilizer
     */
    public abstract void putFlowerFertilizer(FlowerFertilizer flowerFertilizer);
    /**
     * Put multiple flower fertilizers into the storage
     * @param flowerFertilizers the fertilizers
     */
    public abstract void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers);
    /**
     * Put multiple flower fertilizers into the storage
     * @param flowerFertilizers the fertilizers
     * @param transaction transaction
     */
    public abstract void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers, Transaction transaction);

    /**
     * read all available flower fertilizer (without lock)
     * @return list of flower fertilizers if successful, or null otherwise
     */
    public List<FlowerFertilizer> readAllFlowerFertilizer() {
        return readAllFlowerFertilizer(null);
    }
    /**
     * read all available flower fertilizer
     * @param transaction the transaction
     * @return list of flower fertilizers if successful, or null otherwise
     */
    public abstract List<FlowerFertilizer> readAllFlowerFertilizer(Transaction transaction);


    /**
     * Take the amount of vegetable fertilizers from the storage (read + delete)
     * @param amount the amount to get
     * @param transaction the transaction
     * @return list of vegetable fertilizers if successful, or null otherwise
     */
    public abstract List<VegetableFertilizer> getVegetableFertilizer(int amount, Transaction transaction);

    /**
     * Put 1 vegetable fertilizers into the storage
     * @param vegetableFertilizer the fertilizer
     */
    public abstract void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer);
    /**
     * Put multiple vegetable fertilizers into the storage
     * @param vegetableFertilizers the fertilizers
     */
    public abstract void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers);
    /**
     * Put multiple vegetable fertilizers into the storage
     * @param vegetableFertilizers the fertilizers
     * @param t the transaction
     */
    public abstract void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers, Transaction t);

    /**
     * read all available flower fertilizer (without blocking)
     * @return list of flower fertilizers if successful, or null otherwise
     */
    public List<VegetableFertilizer> readAllVegetableFertilizer() {
        return readAllVegetableFertilizer(null);
    }
    /**
     * read all available flower fertilizer
     * @param transaction the transaction
     * @return list of flower fertilizers if successful, or null otherwise
     */
    public abstract List<VegetableFertilizer> readAllVegetableFertilizer(Transaction transaction);


    /**
     * Tries to get the exact amount of soil
     *
     * @param amount the amount
     * @param transaction the transaction
     * @return true if exact amount is available
     */
    public boolean tryGetExactAmountOfSoil(int amount, Transaction transaction) {
        List<SoilPackage> soilPackages = tryGetSoilPackages(amount, transaction);

        if(soilPackages == null) {
            return false;
        }

        int haveAmount = 0;
        for(SoilPackage soilPackage : soilPackages) {
            haveAmount += soilPackage.getAmount();
        }

        if(haveAmount < amount) {
            return false;
        }

        int remaining = haveAmount - amount;
        SoilPackage r = new SoilPackage();
        r.setAmount(remaining);
        putSoilPackage(r, transaction);

        return true;
    }

    /**
     * Tries to get enough soil packages to satisfy the amount.
     * You need to return the packages that you don't need
     *
     * @param amount the amount
     * @param transaction the transaction
     * @return the soil packages if successful, or null otherwise
     */
    public List<SoilPackage> tryGetSoilPackages(int amount, Transaction transaction){
        List<SoilPackage> selectedSoilPackages = new ArrayList<>();

        List<SoilPackage> allSoilPackages = getAllSoilPackages(transaction);

        if(allSoilPackages == null || allSoilPackages.isEmpty()){
            return null;
        }

        allSoilPackages.sort((o1, o2) -> (Integer.compare(o1.getAmount(), o2.getAmount())));

        int index = 0;

        while (amount > 0){

            // if not enough soil packages are available then put them all back into the storage and return null
            if(index >= allSoilPackages.size())
            {
                index = 0;
                selectedSoilPackages = null;
                break;
            }
            selectedSoilPackages.add(allSoilPackages.get(index));
            amount -= allSoilPackages.get(index).getAmount();
            index ++;
        }


        // put back all soil packages that are not needed
        List<SoilPackage> unused = new LinkedList<>();
        while (index < allSoilPackages.size())
        {
            unused.add(allSoilPackages.get(index));
            index++;
        }
        putSoilPackages(unused, transaction);

        return selectedSoilPackages;
    }

    /**
     * Gets (read + delete) all available soil packages
     * @param transaction the transaction
     * @return List of soil packages if successful, or null otherwise
     */
    protected abstract List<SoilPackage> getAllSoilPackages(Transaction transaction);

    /**
     * Put a soil package in the storage
     * @param soilPackage the soil package
     * @param transaction the transaction
     */
    public abstract void putSoilPackage(SoilPackage soilPackage, Transaction transaction);
    /**
     * Put multiple soil packages in the storage
     * @param soilPackage the soil packages
     * @param transaction the transaction
     */
    public abstract void putSoilPackages(List<SoilPackage> soilPackage, Transaction transaction);

    /**
     * Read all available soil packages (without lock)
     * @return all availbale soil packages if successful, or null otherwise
     */
    public List<SoilPackage> readAllSoilPackage() {
        return readAllSoilPackage(null);
    }
    /**
     * Read all available soil packages
     * @param transaction the transaction
     * @return all availbale soil packages if successful, or null otherwise
     */
    public abstract List<SoilPackage> readAllSoilPackage(Transaction transaction);


    /**
     * Blocks until the specified amount of water has been taken
     *
     * @param amount the amount of water
     */
    public void getWater(int amount){
        Water water = new Water();

        int howMany = (int)Math.ceil((float)amount / 250.0f);

        for (int i = 0; i < howMany; i++){
            Water w = accessTap();
            water.setAmount(water.getAmount()+ w.getAmount());
        }
    }

    /**
     * Access the water tap
     * Blocks until 1 water is retrieved
     * @return 1 water
     */
    public abstract Water accessTap();

    /**
     * Put 1 water into the storage
     * @param water water to put
     */
    public abstract void putWater(Water water);
}
