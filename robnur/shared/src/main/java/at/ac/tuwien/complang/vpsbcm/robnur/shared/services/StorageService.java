package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StorageService {

    // TODO: consider abstract method for deciding which plant should be planted
    public abstract Plant getSeed();

    public abstract void putSeed(Plant plant);

    public abstract List<Plant> readAllSeeds();

    // TODO: consider abstract method for delivering already used packages

    protected abstract List<SoilPackage> getAllSoilPackages();

    public abstract void putSoilPackage(SoilPackage soilPackage);

    public abstract List<SoilPackage> readAllSoilPackage();

    public abstract List<FlowerFertilizer> getFlowerFertilizer(int amount);

    public abstract void putFlowerFertilizer(FlowerFertilizer flowerFertilizer);

    public abstract List<FlowerFertilizer> readAllFlowerFertilizer();

    public abstract List<VegetableFertilizer> getVegetableFertilizer(int amount);

    public abstract void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer);

    public abstract List<VegetableFertilizer> readAllVegetableFertilizer();

    public abstract Water accessTap();

    public abstract void putWater(Water water);

    public List<SoilPackage> getSoil(int amount){
        List<SoilPackage> selectedSoilPackages = new ArrayList<>();

        List<SoilPackage> allSoilPackages = getAllSoilPackages();

        if(allSoilPackages.isEmpty()){
            return null;
        }

        Collections.sort(allSoilPackages,
                (o1, o2) -> (new Integer(o1.getAmount()).compareTo(new Integer(o2.getAmount()))));

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
        while (index < allSoilPackages.size())
        {
            putSoilPackage(allSoilPackages.get(index));
            index++;
        }

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
