package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

import java.util.List;

public interface ResearchService {

    void putFlower(Flower flower);

    void putVegetable(Vegetable vegetable);

    void deleteFlower(Flower flower,Transaction transaction);

    void deleteVegetable(Vegetable vegetable,Transaction transaction);

    List<Flower> readAllFlowers(Transaction transaction);

    List<Vegetable> readAllVegetables(Transaction transaction);
}
