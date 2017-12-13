package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.CultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import org.mozartspaces.core.MzsCoreException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SpaceMonitoringRobot {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GreenhouseServiceImpl.class);

    private static GreenhouseService greenhouseService;
    private static TransactionService transactionService;

    private static final int MONITORING_INTERVAL_MS = 2000;
    private static Random rand = new Random();


    public static void main(String[] args) throws URISyntaxException, MzsCoreException, InterruptedException {
        if(args.length != 1) {
            System.err.println("You need to specify the space uri");
            System.exit(1);
        }

        URI uri = new URI(args[0]);
        greenhouseService = new GreenhouseServiceImpl(uri);
        transactionService = new TransactionServiceImpl(uri);

        monitorGreenhouse();
    }

    private static void doGrow(Plant plant, CultivationInformation cultivationInformation) {
        float growthRate = cultivationInformation.getGrowthRate();

        float randomNumber = 0.8f + rand.nextFloat() * (1.2f - 0.8f);

        int add = Math.round(growthRate * randomNumber * 100f);
        plant.setGrowth(Math.min(plant.getGrowth() + add, 100));
    }


    static void monitorVegetables() {

        logger.debug("MONITOR VEGETABLES ");

        // get all available plants
        Transaction t = transactionService.beginTransaction(-1);

        List<VegetablePlant> vegs = greenhouseService.getAllVegetablePlants(t);
        if(vegs == null || vegs.isEmpty()) {
            logger.error("vegetables is empty");
            t.rollback();
            return;
        }

        // grow
        for(VegetablePlant p : vegs) {
            logger.debug("growing vegetables");
            doGrow(p, p.getCultivationInformation());
        }

        // write back plant
        if(!greenhouseService.plantVegetables(vegs, t)) {
            logger.error("could not put plant back - rollback");
            t.rollback();
            return;
        }

        t.commit();
    }

    static void monitorFlowers() {
        logger.debug("MONITOR Flowers ");

        // get all available plants
        Transaction t = transactionService.beginTransaction(-1);

        List<FlowerPlant> flos = greenhouseService.getAllFlowerPlants(t);

        if(flos == null || flos.isEmpty()) {
            logger.error("flowers is empty");
            t.rollback();
            return;
        }

        for(FlowerPlant p : flos) {
            logger.debug("growing flowers");
            doGrow(p, p.getCultivationInformation());
        }

        if(!greenhouseService.plantFlowers(flos, t)) {
            logger.error("could not put flower plant back - rollback");
            t.rollback();
            return;
        }

        logger.info("flower-commit before");
        t.commit();
        logger.info("flower-commit after");

    }

    public static void monitorGreenhouse() {

        while (true) {
            monitorVegetables();
            monitorFlowers();

            try {
                Thread.sleep(MONITORING_INTERVAL_MS);
            } catch (InterruptedException e) {
                logger.trace("EXCEPTION", e);
            }
        }
    }
}
