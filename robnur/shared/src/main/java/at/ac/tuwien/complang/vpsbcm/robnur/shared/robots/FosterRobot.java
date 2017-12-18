package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerPesticide;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetablePesticide;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.util.Date;

public class FosterRobot extends Robot {

    private final static int WAITING_TIME = 1000;

    private GreenhouseService greenhouseService;
    private StorageService storageService;
    private TransactionService transactionService;

    public FosterRobot(String id, GreenhouseService greenhouseService, StorageService storageService, TransactionService transactionService) {
        this.greenhouseService = greenhouseService;
        this.storageService = storageService;
        this.transactionService = transactionService;
        this.setId(id);
    }

    public synchronized void foster() {
        boolean hasFostered;

        do {
            hasFostered = false;

            if(fosterFlower()) {
                hasFostered = true;
            }

            if(fosterVegetable()) {
                hasFostered = true;
            }
        } while(hasFostered && !greenhouseService.isExit());
    }


    private boolean fosterVegetable() {
        if(greenhouseService.isExit()) {
            logger.info("you can quit me now...");
            return false;
        }

        Transaction transaction = transactionService.beginTransaction(-1);

        VegetablePlant plant = greenhouseService.getInfestedVegetablePlant(transaction);
        if(plant == null) {
            logger.debug("got no vegetable plant to foster");
            transaction.rollback();
            return false;
        }

        VegetablePesticide pesticide = storageService.getVegetablePesticide(transaction);
        if(pesticide == null) {
            logger.debug("got no vegetable pesticide");
            transaction.rollback();
            return false;
        }

        doFoster(plant);

        if(!greenhouseService.plant(plant, transaction)) {
            logger.debug("could not put vegetable plant back");
            transaction.rollback();
            return false;
        }

        transaction.commit();
        return true;
    }

    private boolean fosterFlower() {
        if(greenhouseService.isExit()) {
            logger.info("you can quit me now...");
            return false;
        }

        Transaction transaction = transactionService.beginTransaction(-1);

        FlowerPlant plant = greenhouseService.getInfestedFlowerPlant(transaction);
        if(plant == null) {
            logger.debug("got no flower plant to foster");
            transaction.rollback();
            return false;
        }

        FlowerPesticide pesticide = storageService.getFlowerPesticide(transaction);
        if(pesticide == null) {
            logger.debug("got no flower pesticide");
            transaction.rollback();
            return false;
        }

        doFoster(plant);

        if(!greenhouseService.plant(plant, transaction)) {
            logger.debug("could not put flower plant back");
            transaction.rollback();
            return false;
        }

        transaction.commit();
        return true;
    }

    private void doFoster(Plant plant) {
        logger.info(String.format("fostering plant %s (%s)...", plant.getTypeName(), plant.getId()));

        float infestation = plant.getInfestation();
        float infestationBefore = infestation;
        infestation = Math.max(0, infestation-0.25f);
        plant.setInfestation(infestation);



        plant.addFosterRobot(String.format("%s (%d%%, %d%%, %s)", getId(), Math.round(infestationBefore*100), Math.round(infestation*100), new Date()));

        waitFosterTime();
    }

    private void waitFosterTime() {
        logger.debug("waiting foster time...");
        try {
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
