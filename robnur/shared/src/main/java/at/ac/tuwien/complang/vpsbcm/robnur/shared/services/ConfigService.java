package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.LinkedList;
import java.util.List;

public interface ConfigService {

    FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(String id, Transaction transaction);

    VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(String id, Transaction transaction);

    void putFlowerPlantCultivationInformation(FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction);

    void putVegetablePlantCultivationInformation(VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction);

    List<FlowerPlantCultivationInformation> readAllFlowerPlantCultivationInformation(Transaction transaction);

    List<VegetablePlantCultivationInformation> readAllVegetablePlantCultivationInformation(Transaction transaction);
}
