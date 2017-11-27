package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public interface CompostService {

    void putFlowerPlant(FlowerPlant flowerPlant);

    void putVegetablePlant(VegetablePlant vegetablePlant);

    void putFlower(Flower flower);

    void putVegetable(Vegetable vegetable);

    List<FlowerPlant> readAllFlowerPlants();

    List<VegetablePlant> readAllVegetablePlants();

    List<Flower> readAllFlowers();

    List<Vegetable> readAllVegetables();
}
