package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.CultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.util.List;
import java.util.Random;

public class MonitoringRobot extends Robot {
    private GreenhouseService greenhouseService;
    private TransactionService transactionService;

    private Random rand = new Random();

    public MonitoringRobot(GreenhouseService greenhouseService, TransactionService transactionService) {
        this.greenhouseService = greenhouseService;
        this.transactionService = transactionService;
    }

    private void doGrow(Plant plant, CultivationInformation cultivationInformation) {
        float growthRate = cultivationInformation.getGrowthRate();

        float randomNumber = 0.8f + rand.nextFloat() * (1.2f - 0.8f);

        int add = Math.round(growthRate * randomNumber * 100f);
        plant.setGrowth(Math.min(plant.getGrowth() + add, 100));
    }

    public void doStuff() {

        while (true) {
            // get all available plants
            Transaction t = transactionService.beginTransaction(-1);

            List<VegetablePlant> vegs = greenhouseService.getAllVegetablePlants(t);
            List<FlowerPlant> flos = greenhouseService.getAllFlowerPlants(t);

            // grow
            for(VegetablePlant p : vegs) {
                doGrow(p, p.getCultivationInformation());
            }
            for(FlowerPlant p : flos) {
                doGrow(p, p.getCultivationInformation());
            }

            // write back plant
            for(VegetablePlant p : vegs) {
                greenhouseService.plant(p, t);
            }
            for(FlowerPlant p : flos) {
                greenhouseService.plant(p, t);
            }

            t.commit();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
