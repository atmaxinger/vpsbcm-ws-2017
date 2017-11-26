package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

import java.util.List;

public interface PackingService {

    void putFlower(Flower flower);

    void putVegetable(Vegetable vegetable);

    Flower getFlower(String flowerId, Transaction transaction);

    Vegetable getVegetable(String vegetableId, Transaction transaction);

    List<Flower> readAllFlowers(Transaction transaction);

    List<Vegetable> readAllVegetables(Transaction transaction);
}
