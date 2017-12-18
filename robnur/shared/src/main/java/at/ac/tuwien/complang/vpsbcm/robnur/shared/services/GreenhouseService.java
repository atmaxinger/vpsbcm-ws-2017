package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class GreenhouseService implements Exitable {

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

    /**
     * takes all vegetable plants
     * @param transaction
     * @return all vegetable plants, empty list if no vegetable plants were found, null if unsuccessful
     */
    public abstract List<VegetablePlant> getAllVegetablePlants(Transaction transaction);

    /**
     * takes all flower plants
     * @param transaction
     * @return all flower plants, empty list if no flower plants were found, null if unsuccessful
     */
    public abstract List<FlowerPlant> getAllFlowerPlants(Transaction transaction);


    /**
     * read all vegetable plants
     * @param transaction
     * @return all vegetable plants, empty list if no vegetable plants were found, null if unsuccessful
     */
    public abstract List<VegetablePlant> readAllVegetablePlants(Transaction transaction);
    public List<VegetablePlant> readAllVegetablePlants() {
        return readAllVegetablePlants(null);
    }

    /**
     * read all flower plants
     * @param transaction
     * @return all flower plants, empty list if no flower plants were found, null if unsuccessful
     */
    public abstract List<FlowerPlant> readAllFlowerPlants(Transaction transaction);
    public List<FlowerPlant> readAllFlowerPlants() {
        return readAllFlowerPlants(null);
    }

    /**
     * takes a vegetable plant form the greenhouse that can be harvested (growth >= 100)
     * @param transaction
     * @return a vegetable plant that can be harvested, null if no harvestable vegetable plant was found
     */
    public abstract VegetablePlant getHarvestableVegetablePlant(Transaction transaction);

    /**
     * takes a flower plant form the greenhouse that can be harvested (growth >= 100)
     * @param transaction
     * @return a flower plant that can be harvested, null if no harvestable flower plant was found
     */
    public abstract FlowerPlant getHarvestableFlowerPlant(Transaction transaction);

    /**
     * takes a vegetable plant form the greenhouse that is limp
     * @param transaction
     * @return a vegetable plant that is limp, null if no limp vegetable plant was found
     */
    public abstract VegetablePlant getLimpVegetablePlant(Transaction transaction);

    /**
     * takes a flower plant form the greenhouse that is limp
     * @param transaction
     * @return a flower plant that is limp, null if no limp flower plant was found
     */
    public abstract FlowerPlant getLimpFlowerPlant(Transaction transaction);

    /**
     * takes a flower plant from the greenhouse that is infested (infestation >= 0.2) but not yet limp
     * @param transaction
     * @return
     */
    public abstract FlowerPlant getInfestedFlowerPlant(Transaction transaction);

    /**
     * takes a vegetable plant from the greenhouse that is infested (infestation >= 0.2) but not yet limp
     * @param transaction
     * @return
     */
    public abstract VegetablePlant getInfestedVegetablePlant(Transaction transaction);
}
