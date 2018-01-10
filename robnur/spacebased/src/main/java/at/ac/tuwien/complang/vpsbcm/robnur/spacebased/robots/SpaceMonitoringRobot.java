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
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SpaceMonitoringRobot {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GreenhouseServiceImpl.class);

    private static GreenhouseService greenhouseService;
    private static TransactionService transactionService;

    private static final int MONITORING_INTERVAL_MS = 2000;
    private static Random rand = new Random();
    private static boolean exit = false;
    private static ContainerReference monitorTokenContainer;
    private static Capi capi;
    private static MzsCore core;

    public static void main(String[] args) throws URISyntaxException, InterruptedException, MzsCoreException {
        if(args.length != 1) {
            logger.fatal("You need to specify the space uri");
            System.exit(1);
        }

        URI uri = new URI(args[0]);

        core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        try {
            monitorTokenContainer = capi.createContainer("monitorTokenContainer",uri,0,null,null);
        } catch (MzsCoreException e) {
            logger.info("There is already one active monitoring robot.");
            core.shutdown(true);
            return;
        }

        greenhouseService = new GreenhouseServiceImpl(uri);
        transactionService = new TransactionServiceImpl(uri);

        UserInputListener userInputListener = new UserInputListener();
        Thread thread = new Thread(userInputListener);
        thread.start();

        monitorGreenhouse();
    }

    private static void doGrow(Plant plant, CultivationInformation cultivationInformation) {

        if(plant.getGrowth() >= 100) {
            return;
        }

        int random = rand.nextInt(100);
        if(random <= cultivationInformation.getVulnerability()) {
            float infestation = Math.min(plant.getInfestation()+0.1f, 1);
            plant.setInfestation(infestation);

            if(infestation == 1) {
                plant.setGrowth(Plant.STATUS_LIMP);
            }
        }

        if(plant.getGrowth() == Plant.STATUS_LIMP) {
            return;
        }

        float growthRate = cultivationInformation.getGrowthRate();

        // TODO: restore old growing method
        float randomNumber = 0.8f + rand.nextFloat() * (1.2f - 0.8f);
        //float randomNumber = 0.1f;

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
            logger.debug("growing vegetables");
            doGrow(p, p.getCultivationInformation());
        }

        // write back plant
        if(!greenhouseService.plantVegetables(vegs, t)) {
            t.rollback();
            return;
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
            logger.debug("growing flowers");
            doGrow(p, p.getCultivationInformation());
        }

        if(!greenhouseService.plantFlowers(flos, t)) {
            t.rollback();
            return;
        }

        t.commit();

    }

    public static void monitorGreenhouse() {

        while (!exit) {
            monitorVegetables();
            monitorFlowers();

            try {
                Thread.sleep(MONITORING_INTERVAL_MS);
            } catch (InterruptedException e) {
                logger.trace("EXCEPTION", e);
            }
        }

        try {
            capi.destroyContainer(monitorTokenContainer,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } finally {
            core.shutdown(true);
            greenhouseService.setExit(true);
        }
    }

    private static class UserInputListener implements Runnable{

        @Override
        public void run() {
            Scanner scanner = new Scanner (System.in);

            while(!scanner.hasNext("exit")){ scanner.next();}
            exit = true;
        }
    }
}
