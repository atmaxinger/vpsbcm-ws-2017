package at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.PostgresHelper;
import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.MonitoringRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

public class PostgresMonitoringRobot {

    private static GreenhouseService greenhouseService;
    private static TransactionService transactionService;

    public static void main(String[] args) {

        greenhouseService = new GreenhouseServiceImpl();
        transactionService = new TransactionServiceImpl();

        //MonitoringRobot monitoringRobot = new MonitoringRobot(greenhouseService, transactionService);

        monitorGreenhouse();

        /*FlowerPlantCultivationInformation flowerPlantCultivationInformation1 = new FlowerPlantCultivationInformation();
        flowerPlantCultivationInformation1.setFlowerType(FlowerType.ROSE);
        flowerPlantCultivationInformation1.setGrowthRate(0.2f);

        FlowerPlant flowerPlant1 = new FlowerPlant();
        flowerPlant1.setCultivationInformation(flowerPlantCultivationInformation1);

        Transaction transaction = transactionService.beginTransaction(-1);
        greenhouseService.plant(flowerPlant1,transaction);
        transaction.commit();

        /*Statement stmt = null;
        try {
            stmt = PostgresHelper.getNewConnection().createStatement();
            stmt.execute("NOTIFY gfp_notify");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

    }

    private final int MONITORING_INTERVAL_MS = 2000;


    private static void doGrow(Plant plant, CultivationInformation cultivationInformation) {
        Random rand = new Random();

        float growthRate = cultivationInformation.getGrowthRate();

        float randomNumber = 0.8f + rand.nextFloat() * (1.2f - 0.8f);

        int add = Math.round(growthRate * randomNumber * 100f);
        plant.setGrowth(Math.min(plant.getGrowth() + add, 100));
    }


    static void monitorVegetables() {

        // get all available plants
        Transaction t = transactionService.beginTransaction(-1);

        List<VegetablePlant> vegs = greenhouseService.getAllVegetablePlants(t);
        if(vegs == null || vegs.isEmpty()) {
            t.rollback();
            return;
        }

        // grow
        for(VegetablePlant p : vegs) {
            doGrow(p, p.getCultivationInformation());
        }

        // write back plant
        for(VegetablePlant p : vegs) {
            greenhouseService.plant(p, t);
        }

        t.commit();
    }

    static void monitorFlowers() {
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
            greenhouseService.plant(p, t);
        }

        t.commit();
    }

    public static void monitorGreenhouse() {

        while (true) {
            monitorVegetables();
            monitorFlowers();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
