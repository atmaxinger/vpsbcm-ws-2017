package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class StorageService {


    public interface Callback<T> {
        void handle(T data);
    }


    private Callback<List<FlowerPlant>> flowerSeedsChanged;
    private Callback<List<VegetablePlant>> vegetableSeedChanged;
    private Callback<List<SoilPackage>> soilPackagesChanged;
    private Callback<List<FlowerFertilizer>> flowerFertilizerChanged;
    private Callback<List<VegetableFertilizer>> vegetableFertilizerChanged;

    protected void notifyFlowerSeedsChanged(List<FlowerPlant> list) {
        if(flowerSeedsChanged != null) {
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
            soilPackagesChanged.handle(list);
        }
    }

    protected void notifyFlowerFertilizerChanged(List<FlowerFertilizer> list) {
        if(flowerFertilizerChanged != null) {
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
     *
     * @param type The type of the seed
     * @param transaction the transaction
     * @return seed that can be planted or null
     */
    public VegetablePlant tryGetSeed(VegetableType type, Transaction transaction) {
        List<VegetablePlant> availiableSeeds = getSeeds(type, transaction);
        int numberOfFertilizers = readAllVegetableFertilizer(transaction).size();
        int soilAmount = availableSoilAmount(transaction);

        for(VegetablePlant plant : availiableSeeds) {
            if(plant.getCultivationInformation().getVegetableType() == type) {
                if(numberOfFertilizers >= plant.getCultivationInformation().getFertilizerAmount()) {
                    if(soilAmount >= plant.getCultivationInformation().getSoilAmount()) {
                        availiableSeeds.remove(plant);
                        // Put remaining seeds back
                        putVegetableSeeds(availiableSeeds, transaction);
                        return plant;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Trys to get a seed of the specified type that can be planted.
     * This means that there is enough fertilizer and enough soil available.
     *
     * @param type The type of the seed
     * @param transaction the transaction
     * @return seed that can be planted or null
     */
    public FlowerPlant tryGetSeed(FlowerType type, Transaction transaction) {
        List<FlowerPlant> availiableSeeds = getSeeds(type, transaction);
        int numberOfFertilizers = readAllFlowerFertilizer(transaction).size();
        int soilAmount = availableSoilAmount(transaction);

        for (FlowerPlant plant : availiableSeeds) {
            if (plant.getCultivationInformation().getFlowerType() == type) {
                if (numberOfFertilizers >= plant.getCultivationInformation().getFertilizerAmount()) {
                    if (soilAmount >= plant.getCultivationInformation().getSoilAmount()) {
                        availiableSeeds.remove(plant);
                        // Put remaining seeds back
                        putFlowerSeeds(availiableSeeds, transaction);
                        return plant;
                    }
                }
            }
        }

        return null;
    }

    protected abstract List<FlowerPlant> getSeeds(FlowerType type, Transaction transaction);
    protected abstract List<VegetablePlant> getSeeds(VegetableType type, Transaction transaction);

    public abstract void putSeed(FlowerPlant plant, Transaction transaction);
    public abstract void putSeed(VegetablePlant plant, Transaction transaction);

    public abstract void putFlowerSeeds(List<FlowerPlant> plants, Transaction transaction);
    public abstract void putVegetableSeeds(List<VegetablePlant> plants, Transaction transaction);


    public List<FlowerPlant> readAllFlowerSeeds() {
        return readAllFlowerSeeds(null);
    }
    public abstract List<FlowerPlant> readAllFlowerSeeds(Transaction transaction);
    public List<VegetablePlant> readAllVegetableSeeds() {
        return readAllVegetableSeeds(null);
    }
    public abstract List<VegetablePlant> readAllVegetableSeeds(Transaction transaction);

    protected abstract List<SoilPackage> getAllSoilPackages(Transaction transaction);

    public abstract void putSoilPackage(SoilPackage soilPackage, Transaction transaction);
    public abstract void putSoilPackages(List<SoilPackage> soilPackage, Transaction transaction);


    public int availableSoilAmount(Transaction t) {
        List<SoilPackage> soilPackages = readAllSoilPackage(null);
        int available = 0;

        for(SoilPackage soilPackage : soilPackages) {
            available += soilPackage.getAmount();
        }

        return available;
    }
    public List<SoilPackage> readAllSoilPackage() {
        return readAllSoilPackage(null);
    }
    public abstract List<SoilPackage> readAllSoilPackage(Transaction transaction);

    public abstract List<FlowerFertilizer> getFlowerFertilizer(int amount, Transaction transaction);

    public abstract void putFlowerFertilizer(FlowerFertilizer flowerFertilizer);
    public abstract void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers);


    public List<FlowerFertilizer> readAllFlowerFertilizer() {
        return readAllFlowerFertilizer(null);
    }
    public abstract List<FlowerFertilizer> readAllFlowerFertilizer(Transaction transaction);

    public abstract List<VegetableFertilizer> getVegetableFertilizer(int amount, Transaction transaction);

    public abstract void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer);
    public abstract void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers);


    public List<VegetableFertilizer> readAllVegetableFertilizer() {
        return readAllVegetableFertilizer(null);
    }
    public abstract List<VegetableFertilizer> readAllVegetableFertilizer(Transaction transaction);

    public abstract Water accessTap();

    public abstract void putWater(Water water);


    /**
     * Trys to get the exact amount of soil
     *
     * @param amount
     * @param transaction
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
     * Trys to get enough soil packages to satisfy the amount.
     * You need to return the packages that you don't need
     *
     * @param amount
     * @param transaction
     * @return
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

    public Water getWater(int amount){
        Water water = new Water();

        int howMany = (int)Math.ceil((float)amount / 250.0f);

        for (int i = 0; i < howMany; i++){
            Water w = accessTap();
            water.setAmount(water.getAmount()+ w.getAmount());
        }

        return water;
    }
}
