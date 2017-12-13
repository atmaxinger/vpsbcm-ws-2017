package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;

import java.util.List;

public abstract class ConfigService implements Exitable {

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

    /**
     * takes the flower plant cultivation information of a specific flower type from the list of cultivation information
     * @param flowerType
     * @param transaction
     * @return cultivation information of the specified flower type or null if not found
     */
    public abstract FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(FlowerType flowerType, Transaction transaction);

    /**
     * takes the vegetable plant cultivation information of a specific vegetable type from the list of cultivation information
     * @param vegetableType
     * @param transaction
     * @return cultivation information of the specified vegetable type or null if not found
     */
    public abstract VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(VegetableType vegetableType, Transaction transaction);

    /**
     * puts a flower plant cultivation information in the list of cultivation information
     * @param flowerPlantCultivationInformation
     * @param transaction
     */
    public abstract void putFlowerPlantCultivationInformation(FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction);

    /**
     * puts a flower plant cultivation information in the list of cultivation information
     * @param vegetablePlantCultivationInformation
     * @param transaction
     */
    public abstract void putVegetablePlantCultivationInformation(VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction);

    /**
     * reads all flower plant cultivation information
     * @param transaction
     * @return all flower plant cultivation information, empty list if no vegetables were found, null if unsuccessful
     */
    public abstract List<FlowerPlantCultivationInformation> readAllFlowerPlantCultivationInformation(Transaction transaction);

    /**
     * reads all vegetable plant cultivation information
     * @param transaction
     * @return all vegetable plant cultivation information, empty list if no vegetables were found, null if unsuccessful
     */
    public abstract List<VegetablePlantCultivationInformation> readAllVegetablePlantCultivationInformation(Transaction transaction);
}
