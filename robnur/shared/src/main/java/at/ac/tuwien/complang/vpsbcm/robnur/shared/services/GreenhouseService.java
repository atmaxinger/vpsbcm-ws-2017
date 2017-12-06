package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class GreenhouseService {

    protected StorageService.Callback<List<Plant>> greenhouseChanged;
    public void onGreenhouseChanged(StorageService.Callback<List<Plant>> greenhouseChanged) {
        this.greenhouseChanged = greenhouseChanged;
    }

    public abstract boolean plant(VegetablePlant vegetablePlant, Transaction transaction);

    public abstract boolean plant(FlowerPlant flowerPlant, Transaction transaction);

    public List<Vegetable> tryHarvestVegetablePlant(Transaction transaction) {
        VegetablePlant plant = getHarvestableVegetablePlant(transaction);

        if(plant != null) {
            List<Vegetable> vegetables = Vegetable.harvestVegetablesFormPlant(plant);

            // if this plant can still be harvested then "plant" it again
            if (plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
                this.plant(plant, transaction);
            }

            return vegetables;
        }

        return null;
    }

    public List<Flower> tryHarvestFlowerPlant(Transaction transaction) {
        FlowerPlant plant = getHarvestableFlowerPlant(transaction);

        if(plant != null) {
            return Flower.harvestFlowerFromFlowerPlant(plant);
        }

        return null;
    }

    public abstract List<VegetablePlant> getAllVegetablePlants(Transaction transaction);
    public abstract List<FlowerPlant> getAllFlowerPlants(Transaction transaction);



    public List<VegetablePlant> readAllVegetablePlants() {
        return readAllVegetablePlants(null);
    }
    public abstract List<VegetablePlant> readAllVegetablePlants(Transaction transaction);
    public List<FlowerPlant> readAllFlowerPlants() {
        return readAllFlowerPlants(null);
    }
    public abstract List<FlowerPlant> readAllFlowerPlants(Transaction transaction);



    protected abstract VegetablePlant getHarvestableVegetablePlant(Transaction t);
    protected abstract FlowerPlant getHarvestableFlowerPlant(Transaction t);
}
