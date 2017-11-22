package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

import java.util.List;

public interface PackingService {

    void putFlower(Flower flower);

    void putVegetable(Vegetable vegetable);

    // TODO: consider implementing abstract method that gets all the different flowers for a bouquet
    List<Flower> getFlowersForBouquet(int amount);

    // TODO: consider implementing abstract method that gets all the different vegetables for a basket
    List<Flower> getVegetableForBasket(int amount);

    List<Flower> readAllFlowers();

    List<Vegetable> readAllVegetables();
}
