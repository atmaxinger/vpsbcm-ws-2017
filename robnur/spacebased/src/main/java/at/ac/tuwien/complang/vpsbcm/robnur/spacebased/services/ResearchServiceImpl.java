package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.ResearchRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ResearchServiceImpl extends ResearchService {

    Capi capi;

    ContainerReference flowerContainer;
    ContainerReference vegetableContainer;
    NotificationManager notificationManager;

    public ResearchServiceImpl(URI spaceUri) {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(),new QueryCoordinator());

        try {
            flowerContainer = CapiUtil.lookupOrCreateContainer("flowerContainer",spaceUri,coordinators,null,capi);
            vegetableContainer = CapiUtil.lookupOrCreateContainer("vegetableContainer",spaceUri,coordinators,null,capi);

            notificationManager.createNotification(flowerContainer, (notification, operation, list) -> notifyFlowersChanged(readAllFlowers(null)), Operation.WRITE, Operation.DELETE, Operation.TAKE);
            notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> notifyVegetablesChanged(readAllVegetables(null)), Operation.WRITE, Operation.DELETE, Operation.TAKE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (MzsCoreException e) {
            e.printStackTrace();
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
    public void deleteFlower(Flower flower, Transaction transaction) {
        ServiceUtil.deleteItemById(flower.getId(),flowerContainer,transaction,capi);
    }

    @Override
    public void deleteVegetable(Vegetable vegetable,Transaction transaction) {
        ServiceUtil.deleteItemById(vegetable.getId(),flowerContainer,transaction,capi);
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
            notificationManager.createNotification(flowerContainer, (notification, operation, list) -> researchRobot.tryUpgradeFlowerPlant(), Operation.WRITE);
            notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> researchRobot.tryUpgradeVegetablePlant(),Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
