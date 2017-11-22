package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

import java.util.List;

public interface ResearchService {

    Flower getFlowerOfSameType(int amount);

    Vegetable getVegetableOfSameType(int amount);

    void putFlower(Flower flower);

    void putVegetable(Vegetable vegetable);

    List<Flower> readAllFlowers();

    List<Vegetable> readAllVegetables();
}
