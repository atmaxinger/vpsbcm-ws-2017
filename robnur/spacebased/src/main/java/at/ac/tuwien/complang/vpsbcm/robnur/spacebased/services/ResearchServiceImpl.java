package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ResearchServiceImpl extends ResearchService {
    final static Logger logger = Logger.getLogger(PackingServiceImpl.class);

    Capi capi;

    ContainerReference flowerContainer;
    ContainerReference vegetableContainer;
    NotificationManager notificationManager;

    List<Notification> notifications = new LinkedList<>();

    boolean exit = false;

    @Override
    public synchronized boolean isExit() {
        return exit;
    }

    @Override
    public synchronized void setExit(boolean exit) {
        this.exit = exit;
    }

    public ResearchServiceImpl(URI spaceUri) {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(),new QueryCoordinator());

        try {
            flowerContainer = CapiUtil.lookupOrCreateContainer("researchFlowerContainer",spaceUri,coordinators,null,capi);
            vegetableContainer = CapiUtil.lookupOrCreateContainer("researchVegetableContainer",spaceUri,coordinators,null,capi);

            notifications.add(notificationManager.createNotification(flowerContainer, (notification, operation, list) -> notifyFlowersChanged(readAllFlowers(null)), Operation.WRITE, Operation.DELETE, Operation.TAKE));
            notifications.add(notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> notifyVegetablesChanged(readAllVegetables(null)), Operation.WRITE, Operation.DELETE, Operation.TAKE));
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }
        catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    public void putFlower(Flower flower, Transaction transaction) {
        ServiceUtil.writeItem(flower,flowerContainer,transaction,capi);
    }

    @Override
    public void putVegetable(Vegetable vegetable, Transaction transaction) {
        ServiceUtil.writeItem(vegetable,vegetableContainer,transaction,capi);
    }

    @Override
    public List<Flower> getAllFlowers(Transaction transaction) {
        return ServiceUtil.getAllItems(flowerContainer,transaction,capi);
    }

    @Override
    public List<Vegetable> getAllVegetables(Transaction transaction) {
        return ServiceUtil.getAllItems(vegetableContainer,transaction,capi);
    }

    @Override
    public List<Flower> readAllFlowers(Transaction transaction) {
        return ServiceUtil.readAllItems(flowerContainer,transaction,capi);
    }

    @Override
    public List<Vegetable> readAllVegetables(Transaction transaction) {
        return ServiceUtil.readAllItems(vegetableContainer,transaction,capi);
    }

    public void registerResearchRobot(ResearchRobot researchRobot){
        try {
            Notification notificationFlowerContainer = notificationManager.createNotification(flowerContainer, (notification, operation, list) -> researchRobot.tryUpgradeFlowerPlant(), Operation.WRITE);
            Notification notificationVegetableContainer = notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> researchRobot.tryUpgradeVegetablePlant(),Operation.WRITE);

            notifications.add(notificationFlowerContainer);
            notifications.add(notificationVegetableContainer);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }
    }
}
