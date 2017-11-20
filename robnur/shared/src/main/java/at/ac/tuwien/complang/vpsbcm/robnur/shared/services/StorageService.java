package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.PlantFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;

import java.util.List;

public interface StorageService {

    // TODO: consider abstract method for deciding which plant should be planted
    Plant getPlantSeed();

    // TODO: consider abstract method for delivering already used packages
    List<SoilPackage> getSoil(int amount);

    void putBackSoil(SoilPackage soilPackage);

    List<PlantFertilizer> getPlantFertilizer(int amount);

    List<VegetableFertilizer> getVegetableFertilizer(int amount);

    Water getWater(int amount);
}
