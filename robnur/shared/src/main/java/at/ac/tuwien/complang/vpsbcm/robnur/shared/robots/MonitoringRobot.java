package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.CultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TranscationService;

import java.util.List;
import java.util.Random;

public class MonitoringRobot extends Robot {
    GreenhouseService greenhouseService;
    TranscationService transactionService;

    public MonitoringRobot(GreenhouseService greenhouseService, TranscationService transactionService) {
        this.greenhouseService = greenhouseService;
        this.transactionService = transactionService;
    }

    public void doStuff() {
        Random rand = new Random();

        while (true) {
            // get all availiable plants
            Transaction t = transactionService.beginTransaction(-1);

            List<VegetablePlant> vegs = greenhouseService.getAllVegetablePlants(t);
            List<FlowerPlant> flos = greenhouseService.getAllFlowerPlants(t);

            // grow
            for(VegetablePlant p : vegs) {
                CultivationInformation cultivationInformation = p.getCultivationInformation();
                float growthRate = cultivationInformation.getGrowthRate();

                float randomNumber = 0.8f + rand.nextFloat() * (1.2f - 0.8f);

                int add = Math.round(growthRate * randomNumber * 100f);
                p.setGrowth(p.getGrowth() + add);
            }

            for(FlowerPlant p : flos) {
                CultivationInformation cultivationInformation = p.getCultivationInformation();
                float growthRate = cultivationInformation.getGrowthRate();

                float randomNumber = 0.8f + rand.nextFloat() * (1.2f - 0.8f);

                int add = Math.round(growthRate * randomNumber * 100f);
                p.setGrowth(p.getGrowth() + add);
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
