package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.CultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MonitoringRobot extends Robot {
    private final int MONITORING_INTERVAL_MS = 2000;

    private GreenhouseService greenhouseService;
    private TransactionService transactionService;

    private Random rand = new Random();

    private List<String> takenVegetableIds = new LinkedList<>();
    private List<String> putVegetableIds = new LinkedList<>();

    private String formatList(List<String> list) {
        String s = "";

        for(int i=0; i<list.size(); i++) {
            s += list.get(i);
            if(i < list.size()-1) {
                s+=", ";
            }
        }

        return s;
    }

    private List<String> getMissingIds(List<String> takenIds, List<String> putIds) {
        List<String> missing = new LinkedList<>();

        for (String planted : takenIds) {
            if(!putIds.contains(planted)) {
                missing.add(planted);
            }
        }

        return missing;
    }

    private List<String> getDoubleIds(List<String> list){
        List<String> copy = new ArrayList<>();
        List<String> restult = new ArrayList<>();

        for (String s: list) {
            if(!copy.contains(s)){
                copy.add(s);
            }
            else {
              restult.add(s);
            }
        }

        return restult;
    }

    private void outputStatistics() {
        if(getMissingIds(takenVegetableIds, putVegetableIds).size() > 0){
            logger.error("--- MISSING VEGETABLES: " + formatList(getMissingIds(takenVegetableIds, putVegetableIds)));
        }
        if(getDoubleIds(putVegetableIds).size() > 0) {
            logger.error("--- Double Vegetables in putVegetableIds: " + formatList(getDoubleIds(putVegetableIds)));
        }
        if(getDoubleIds(takenVegetableIds).size() > 0) {
            logger.error("--- Double Vegetables in takenVegetablesIds: " + formatList(getDoubleIds(takenVegetableIds)));
        }
        if(takenVegetableIds.size() != putVegetableIds.size()){
            logger.error("takenVegetableIds != putVegetableIds");
            logger.error("takenVegetableIds");
            for (String s:takenVegetableIds) {
                logger.error(s);
            }
            logger.error("putVegetableIds");
            for (String s:putVegetableIds) {
                logger.error(s);
            }
        }
    }

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


    void monitorVegetables() {

        // get all available plants
        Transaction t = transactionService.beginTransaction(-1);

        List<VegetablePlant> vegs = greenhouseService.getAllVegetablePlants(t);
        if(vegs == null || vegs.isEmpty()) {
            t.rollback();
            return;
        }

        for (VegetablePlant v:vegs) {
            takenVegetableIds.add(v.getId());
        }

        // grow
        for(VegetablePlant p : vegs) {
            doGrow(p, p.getCultivationInformation());
        }

        // write back plant
        for(VegetablePlant p : vegs) {
            if(!greenhouseService.plant(p, t)) {
                System.err.println("could not put plant back - rollback");
                t.rollback();
                return;
            }

            putVegetableIds.add(p.getId());
        }

        t.commit();

        outputStatistics();
    }

    void monitorFlowers() {
        // get all available plants
        Transaction t = transactionService.beginTransaction(-1);

        List<FlowerPlant> flos = greenhouseService.getAllFlowerPlants(t);

        if(flos == null || flos.isEmpty()) {
            t.rollback();
            return;
        }

        for(FlowerPlant p : flos) {
            doGrow(p, p.getCultivationInformation());
        }

        for(FlowerPlant p : flos) {
            if(!greenhouseService.plant(p, t)) {
                System.err.println("could not put plant back - rollback");
                t.rollback();
                return;
            }
        }

        t.commit();
    }

    public void monitorGreenhouse() {

        while (true) {
            takenVegetableIds.clear();
            putVegetableIds.clear();

            monitorVegetables();
            monitorFlowers();

            try {
                Thread.sleep(MONITORING_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
