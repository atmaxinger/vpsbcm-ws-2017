package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;

import java.util.List;

public abstract class PackingService implements Exitable {

    private StorageService.Callback<List<Flower>> flowersChanged;
    private StorageService.Callback<List<Vegetable>> vegetablesChanged;
    public void onFlowersChanged(StorageService.Callback<List<Flower>> flowersChanged) {
        this.flowersChanged = flowersChanged;
    }

    public void onVegetablesChanged(StorageService.Callback<List<Vegetable>> vegetablesChanged) {
        this.vegetablesChanged = vegetablesChanged;
    }

    protected void raiseFlowersChanged() {
        if(flowersChanged != null) {
            flowersChanged.handle(readAllFlowers(null));
        }
    }

    protected void raiseVegetablesChanged() {
        if(vegetablesChanged != null) {
            vegetablesChanged.handle(readAllVegetables(null));
        }
    }

    /**
     * puts a flower into the packing department
     * @param flower
     * @param transaction
     */
    public abstract void putFlower(Flower flower, Transaction transaction);

    /**
     * puts a vegetable into the packing department
     * @param vegetable
     * @param transaction
     */
    public abstract void putVegetable(Vegetable vegetable,Transaction transaction);

    /**
     * takes a specific flower form the packing department
     * @param flowerId id of the flower
     * @param transaction
     * @return the flower with the specified id, null if flower does not exist, null if unsuccessful
     */
    public abstract Flower getFlower(String flowerId, Transaction transaction);

    /**
     * takes a specific vegetable form the packing department
     * @param vegetableId id of the vegetable
     * @param transaction
     * @return the vegetable with the specified id, null if vegetable does not exist, null if unsuccessful
     */
    public abstract Vegetable getVegetable(String vegetableId, Transaction transaction);

    /**
     * reads all flowers that are in the packing department
     * @param transaction
     * @return all flowers that are in the packing department, empty list if there are no flowers in the packing department, null if unsuccessful
     */
    public abstract List<Flower> readAllFlowers(Transaction transaction);

    /**
     * reads all vegetables that are in the packing department
     * @param transaction
     * @return all vegetables that are in the packing department, empty list if there are no vegetables in the packing department, null if unsuccessful
     */
    public abstract List<Vegetable> readAllVegetables(Transaction transaction);

    /**
     * gets a vegetable of a specific type
     * @param type type of the vegetable
     * @param transaction
     * @return a vegetable of the specified type, null if there is no vegetable of such a type, null if unsuccessful
     */
    public abstract Vegetable getVegetableByType(VegetableType type, Transaction transaction);

    /**
     * gets a flower of a specific type
     * @param type type of the flower
     * @param transaction
     * @return a flower of the specified type, null if there is no flower of such a type, null if unsuccessful
     */
    public abstract Flower getFlowerByType(FlowerType type, Transaction transaction);
}
