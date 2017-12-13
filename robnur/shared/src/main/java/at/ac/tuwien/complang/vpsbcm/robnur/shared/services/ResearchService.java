package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

import java.util.List;

public abstract class ResearchService {
    private StorageService.Callback<List<Flower>> flowersChanged;
    private StorageService.Callback<List<Vegetable>> vegetablesChanged;

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

    public void onFlowersChanged(StorageService.Callback<List<Flower>> flowersChanged) {
        this.flowersChanged = flowersChanged;
    }

    public void onVegetablesChanged(StorageService.Callback<List<Vegetable>> vegetablesChanged) {
        this.vegetablesChanged = vegetablesChanged;
    }

    /**
     * put flower into research department
     * @param flower
     * @param transaction
     */
    public abstract void putFlower(Flower flower, Transaction transaction);

    /**
     * put vegetable into research department
     * @param vegetable
     * @param transaction
     */
    public abstract void putVegetable(Vegetable vegetable, Transaction transaction);

    /**
     * takes all flowers form the research department
     * @param transaction
     * @return all flowers that are in the research department, empty list if no flowers were found, null if unsuccessful
     */
    public abstract List<Flower> getAllFlowers(Transaction transaction);

    /**
     * takes all vegetables form the research department
     * @param transaction
     * @return all vegetables that are in the research department, empty list if no vegetables were found, null if unsuccessful
     */
    public abstract List<Vegetable> getAllVegetables(Transaction transaction);

    /**
     * reads all flowers from the research department
     * @param transaction
     * @return all flowers that are in the research department, empty list if no flowers were found, null if unsuccessful
     */
    public abstract List<Flower> readAllFlowers(Transaction transaction);

    /**
     * reads all vegetables from the research department
     * @param transaction
     * @return all vegetables that are in the research department, empty list if no vegetables were found, null if unsuccessful
     */
    public abstract List<Vegetable> readAllVegetables(Transaction transaction);

    /**
     * checks if notifications are stopped
     * @return true if notifications are stopped else false
     */
    public abstract boolean isExit();
}
