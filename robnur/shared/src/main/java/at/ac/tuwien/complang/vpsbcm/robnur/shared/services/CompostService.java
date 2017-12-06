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

    public abstract void putFlowerPlant(FlowerPlant flowerPlant);

    public abstract void putVegetablePlant(VegetablePlant vegetablePlant);

    public abstract void putFlower(Flower flower);

    public abstract void putVegetable(Vegetable vegetable);

    public abstract List<FlowerPlant> readAllFlowerPlants();

    public abstract List<VegetablePlant> readAllVegetablePlants();

    public abstract List<Flower> readAllFlowers();

    public abstract List<Vegetable> readAllVegetables();
}
