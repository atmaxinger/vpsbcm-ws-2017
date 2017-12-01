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

    public abstract void putFlower(Flower flower);

    public abstract void putVegetable(Vegetable vegetable);

    public abstract void deleteFlower(Flower flower,Transaction transaction);

    public abstract void deleteVegetable(Vegetable vegetable,Transaction transaction);

    public abstract List<Flower> readAllFlowers(Transaction transaction);

    public abstract List<Vegetable> readAllVegetables(Transaction transaction);
}
