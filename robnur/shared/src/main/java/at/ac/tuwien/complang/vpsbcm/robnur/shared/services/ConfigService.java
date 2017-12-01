package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.LinkedList;
import java.util.List;

public abstract class ConfigService {

    private StorageService.Callback<List<FlowerPlantCultivationInformation>> flowerCultivationInformationChanged;
    private StorageService.Callback<List<VegetablePlantCultivationInformation>> vegetableCultivationInformationChanged;

    public void onFlowerCultivationInformationChanged(StorageService.Callback<List<FlowerPlantCultivationInformation>> flowerCultivationInformationChanged) {
        this.flowerCultivationInformationChanged = flowerCultivationInformationChanged;
    }

    public void onVegetableCultivationInformationChanged(StorageService.Callback<List<VegetablePlantCultivationInformation>> vegetableCultivationInformationChanged) {
        this.vegetableCultivationInformationChanged = vegetableCultivationInformationChanged;
    }

    protected void notifyFlowerCultivationInformationChanged(List<FlowerPlantCultivationInformation> cultivationInformations) {
        if(flowerCultivationInformationChanged != null) {
            flowerCultivationInformationChanged.handle(cultivationInformations);
        }
    }

    protected void notifyVegetableCultivationInformationChanged(List<VegetablePlantCultivationInformation> cultivationInformations) {
        if(vegetableCultivationInformationChanged != null) {
            vegetableCultivationInformationChanged.handle(cultivationInformations);
        }
    }
    public abstract FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(String id, Transaction transaction);

    public abstract VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(String id, Transaction transaction);

    public abstract void deleteFlowerPlantCultivationInformation(String id, Transaction transaction);

    public abstract void deleteVegetablePlantCultivationInformation(String id, Transaction transaction);

    public abstract void putFlowerPlantCultivationInformation(FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction);

    public abstract void putVegetablePlantCultivationInformation(VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction);

    public abstract List<FlowerPlantCultivationInformation> readAllFlowerPlantCultivationInformation(Transaction transaction);

    public abstract List<VegetablePlantCultivationInformation> readAllVegetablePlantCultivationInformation(Transaction transaction);
}
