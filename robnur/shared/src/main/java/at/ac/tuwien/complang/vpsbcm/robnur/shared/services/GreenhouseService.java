package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public interface GreenhouseService {

    void plant(VegetablePlant veg, Transaction t);

    void plant(FlowerPlant plant, Transaction t);

    List<Vegetable> harvestVegetablePlant(Transaction t);

    List<Flower> harvestFlowerPlant(Transaction t);

    List<VegetablePlant> readAllVegetablePlants();

    List<FlowerPlant> readAllFlowerPlants();
}
