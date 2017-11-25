package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public interface GreenhouseService {

    void plant(VegetablePlant vegetablePlant, Transaction transaction);

    void plant(FlowerPlant flowerPlant, Transaction transaction);

    List<Vegetable> harvestVegetablePlant(Transaction transaction);

    List<Flower> harvestFlowerPlant(Transaction transaction);

    List<VegetablePlant> readAllVegetablePlants();

    List<FlowerPlant> readAllFlowerPlants();
}
