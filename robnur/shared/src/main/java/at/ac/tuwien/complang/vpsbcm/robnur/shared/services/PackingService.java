package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;

import java.util.List;

public abstract class PackingService {

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

    public abstract void putFlower(Flower flower, Transaction transaction);

    public abstract void putVegetable(Vegetable vegetable,Transaction transaction);

    public abstract Flower getFlower(String flowerId, Transaction transaction);

    public abstract Vegetable getVegetable(String vegetableId, Transaction transaction);

    public abstract List<Flower> readAllFlowers(Transaction transaction);

    public abstract List<Vegetable> readAllVegetables(Transaction transaction);
}
