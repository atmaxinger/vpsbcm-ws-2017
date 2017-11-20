package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.PlantPoint;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;

public interface GreenhouseService {

    PlantPoint getFreePlantPoint();

    void setPlantPoint(PlantPoint plantPoint);

    PlantPoint<VegetablePlant> getHarvestReadyVegetablePlantPoint();

    PlantPoint<FlowerPlant> getHarvestReadyFlowerPlantPoint();
}
