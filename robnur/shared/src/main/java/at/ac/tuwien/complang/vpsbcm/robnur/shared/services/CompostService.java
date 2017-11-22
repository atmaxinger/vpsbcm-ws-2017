package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;

import java.util.List;

public interface CompostService {

    void putPlant(Plant plant);

    List<Plant> readAllPlants();
}
