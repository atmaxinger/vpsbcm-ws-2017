package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

public interface ResearchService {

    Flower getFlowerOfSameType(int amount);

    Vegetable getVegetableOfSameType(int amount);

    void putFlower(Flower flower);

    void putVegetable(Vegetable vegetable);
}
