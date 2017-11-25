package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;

import java.util.List;

public abstract class GreenhouseService {

    protected StorageService.Callback<List<Plant>> greenhouseChanged;
    public void onGreenhouseChanged(StorageService.Callback<List<Plant>> greenhouseChanged) {
        this.greenhouseChanged = greenhouseChanged;
    }

    public abstract void plant(VegetablePlant vegetablePlant, Transaction transaction);

    public abstract void plant(FlowerPlant flowerPlant, Transaction transaction);

    public abstract List<Vegetable> harvestVegetablePlant(Transaction transaction);

    public abstract List<Flower> harvestFlowerPlant(Transaction transaction);

    public abstract List<VegetablePlant> readAllVegetablePlants();

    public abstract List<FlowerPlant> readAllFlowerPlants();
}
