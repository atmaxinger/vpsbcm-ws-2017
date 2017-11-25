package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;

import java.util.List;
import java.util.Random;

public class PlantAndHarvestRobot extends Robot {

    private StorageService storageService;
    private GreenhouseService greenhouseService;
    private TranscationService transactionService;
    private PackingService packingService;

    public PlantAndHarvestRobot(StorageService storageService, GreenhouseService greenhouseService, TranscationService transactionService, PackingService packingService) {
        this.storageService = storageService;
        this.greenhouseService = greenhouseService;
        this.transactionService = transactionService;
        this.packingService = packingService;
    }


    /**
     *
     * @return true if something has been harvested
     */
    private boolean harvestVegetable() {
        Transaction t = transactionService.beginTransaction(1000);
        List<Vegetable> harvested = greenhouseService.harvestVegetablePlant(t);
        t.commit();

        if(harvested != null && harvested.size() > 0) {
            for(Vegetable veg : harvested) {
                packingService.putVegetable(veg);
            }

            return true;
        }

        return false;
    }

    /**
     *
     * @return true if something has been harvested
     */
    private boolean harvestFlower() {
        Transaction t = transactionService.beginTransaction(1000);
        List<Flower> harvested = greenhouseService.harvestFlowerPlant(t);
        t.commit();

        if(harvested != null && harvested.size() > 0) {
            for(Flower flo : harvested) {
                packingService.putFlower(flo);
            }

            return true;
        }

        return false;
    }

    public void doStuff() {
        Random random = new Random();

        while (true) {

            // harvest old plant
            boolean harvestVegetableFirst = random.nextBoolean();
            if(harvestVegetableFirst) {
                if(!harvestVegetable()) {
                    harvestFlower();
                }
            } else {
                if(!harvestFlower()) {
                    harvestVegetable();
                }
            }

            Transaction t = transactionService.beginTransaction(-1);

            // get seeds

            // get resources

            // plant

            t.commit();
        }
    }
}
