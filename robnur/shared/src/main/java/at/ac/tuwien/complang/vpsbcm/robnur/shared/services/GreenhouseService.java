package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class GreenhouseService {

    protected StorageService.Callback<List<Plant>> greenhouseChanged;
    public void onGreenhouseChanged(StorageService.Callback<List<Plant>> greenhouseChanged) {
        this.greenhouseChanged = greenhouseChanged;
    }

    /**
     * plants a list of vegetable plants in the greenhouse
     * @param vegetablePlants
     * @param transaction
     * @return true if successful otherwise false
     */
    public abstract boolean plantVegetables(List<VegetablePlant> vegetablePlants, Transaction transaction);

    /**
     * plants a list of vegetable plants in the greenhouse
     * @param flowerPlants
     * @param transaction
     * @return true if successful otherwise false
     */
    public abstract boolean plantFlowers(List<FlowerPlant> flowerPlants, Transaction transaction);

    /**
     * plants a vegetable plant in the greenhouse
     * @param vegetablePlant
     * @param transaction
     * @return true if successful otherwise false
     */
    public abstract boolean plant(VegetablePlant vegetablePlant, Transaction transaction);

    /**
     * plants a flower plant in the greenhouse
     * @param flowerPlant
     * @param transaction
     * @return true if successful otherwise false
     */
    public abstract boolean plant(FlowerPlant flowerPlant, Transaction transaction);


    public abstract List<VegetablePlant> getAllVegetablePlants(Transaction transaction);
    public abstract List<FlowerPlant> getAllFlowerPlants(Transaction transaction);



    public abstract List<VegetablePlant> readAllVegetablePlants(Transaction transaction);
    public List<VegetablePlant> readAllVegetablePlants() {
        return readAllVegetablePlants(null);
    }
    public abstract List<FlowerPlant> readAllFlowerPlants(Transaction transaction);
    public List<FlowerPlant> readAllFlowerPlants() {
        return readAllFlowerPlants(null);
    }



    public abstract VegetablePlant getHarvestableVegetablePlant(Transaction t);
    public abstract FlowerPlant getHarvestableFlowerPlant(Transaction t);
}
