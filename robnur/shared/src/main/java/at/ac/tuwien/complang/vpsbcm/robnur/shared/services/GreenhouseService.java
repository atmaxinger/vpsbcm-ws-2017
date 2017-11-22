package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public interface GreenhouseService {

    PlantPoint getFreePlantPoint();

    void setPlantPoint(PlantPoint plantPoint);

    PlantPoint<VegetablePlant> getHarvestReadyVegetablePlantPoint();

    PlantPoint<FlowerPlant> getHarvestReadyFlowerPlantPoint();

    List<VegetablePlant> readAllVegetablePlants();

    List<FlowerPlant> readAllFlowerPlants();
}
