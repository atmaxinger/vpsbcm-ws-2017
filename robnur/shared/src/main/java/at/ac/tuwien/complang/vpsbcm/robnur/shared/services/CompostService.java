package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;

import java.util.List;

public abstract class CompostService {

    private StorageService.Callback<List<FlowerPlant>> flowerPlantsChanged;
    private StorageService.Callback<List<Flower>> flowersChanged;
    private StorageService.Callback<List<VegetablePlant>> vegetablePlantsChanged;
    private StorageService.Callback<List<Vegetable>> vegetablesChanged;

    public void onFlowerPlantsChanged(StorageService.Callback<List<FlowerPlant>> flowerPlantsChanged) {
        this.flowerPlantsChanged = flowerPlantsChanged;
    }

    public void onFlowersChanged(StorageService.Callback<List<Flower>> flowersChanged) {
        this.flowersChanged = flowersChanged;
    }

    public void onVegetablePlantsChanged(StorageService.Callback<List<VegetablePlant>> vegetablePlantsChanged) {
        this.vegetablePlantsChanged = vegetablePlantsChanged;
    }

    public void onVegetablesChanged(StorageService.Callback<List<Vegetable>> vegetablesChanged) {
        this.vegetablesChanged = vegetablesChanged;
    }

    protected void notifyFlowerPlantsChanged(List<FlowerPlant> plants) {
        if(flowerPlantsChanged != null) {
            flowerPlantsChanged.handle(plants);
        }
    }

    protected void notifyVegetablePlantsChanged(List<VegetablePlant> plants) {
        if(vegetablePlantsChanged != null) {
            vegetablePlantsChanged.handle(plants);
        }
    }

    protected void notifyFlowersChanged(List<Flower> flowers) {
        if(flowersChanged != null) {
            flowersChanged.handle(flowers);
        }
    }

    protected void notifyVegetablesChanged(List<Vegetable> vegetables) {
        if(vegetablesChanged != null) {
            vegetablesChanged.handle(vegetables);
        }
    }

    /**
     * puts a flower plant on the compost
     * @param flowerPlant
     * @param transaction
     */
    public abstract void putFlowerPlant(FlowerPlant flowerPlant, Transaction transaction);

    /**
     * puts a vegetable plant on the compost
     * @param vegetablePlant
     * @param transaction
     */
    public abstract void putVegetablePlant(VegetablePlant vegetablePlant, Transaction transaction);

    /**
     * puts a flower on the compost
     * @param flower
     * @param transaction
     */
    public abstract void putFlower(Flower flower, Transaction transaction);

    /**
     * puts a vegetable on the compost
     * @param vegetable
     * @param transaction
     */
    public abstract void putVegetable(Vegetable vegetable, Transaction transaction);

    /**
     * reads all flower plants that are in the compost
     * @return all flower plant that are in the compost, empty list if no flower plants were found, null if unsuccessful
     */
    public abstract List<FlowerPlant> readAllFlowerPlants();

    /**
     * reads all vegetable plants that are in the compost
     * @return all vegetable plant that are in the compost, empty list if no vegetable plants were found, null if unsuccessful
     */
    public abstract List<VegetablePlant> readAllVegetablePlants();

    /**
     * reads all flowers that are in the compost
     * @return all flowers that are in the compost, empty list if no flowers were found, null if unsuccessful
     */
    public abstract List<Flower> readAllFlowers();

    /**
     * reads all vegetables that are in the compost
     * @return all vegetables that are in the compost, empty list if no vegetables were found, null if unsuccessful
     */
    public abstract List<Vegetable> readAllVegetables();
}
