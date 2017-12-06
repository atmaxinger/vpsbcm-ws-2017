package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.util.Arrays;
import java.util.List;
import java.net.URI;

public class PackingServiceImpl extends PackingService {

    Capi capi;

    ContainerReference vegetableContainer;
    ContainerReference flowerContainer;
    NotificationManager notificationManager;

    public PackingServiceImpl(URI spaceUri) {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(), new FifoCoordinator(), new QueryCoordinator());

        try {
            vegetableContainer = CapiUtil.lookupOrCreateContainer("packingVegetableContainer", spaceUri, coordinators, null, capi);
            flowerContainer = CapiUtil.lookupOrCreateContainer("packingFlowerContainer", spaceUri, coordinators, null, capi);

            try {
                notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> raiseVegetablesChanged(), Operation.DELETE, Operation.TAKE, Operation.WRITE);
                notificationManager.createNotification(flowerContainer, (notification, operation, list) -> raiseFlowersChanged(), Operation.DELETE, Operation.TAKE, Operation.WRITE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFlower(Flower flower) {
        ServiceUtil.writeItem(flower,flowerContainer,null,capi);
    }

    @Override
    public void putVegetable(Vegetable vegetable) {
        ServiceUtil.writeItem(vegetable,vegetableContainer,null,capi);
    }

    @Override
    public Flower getFlower(String id, at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction transaction) {
        return ServiceUtil.getItemById(id,flowerContainer,transaction,capi);
    }

    @Override
    public Vegetable getVegetable(String id, at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction transaction) {
        return ServiceUtil.getItemById(id,vegetableContainer,transaction,capi);
    }

    @Override
    public List<Flower> readAllFlowers(at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction transaction) {
        Selector selector = FifoCoordinator.newSelector(FifoCoordinator.FifoSelector.COUNT_MAX);
        return ServiceUtil.readAllItems(flowerContainer,selector,transaction,capi);
    }

    @Override
    public List<Vegetable> readAllVegetables(Transaction transaction) {
        Selector selector = FifoCoordinator.newSelector(FifoCoordinator.FifoSelector.COUNT_MAX);
        return ServiceUtil.readAllItems(vegetableContainer,selector,transaction,capi);
    }

    public void registerPackRobot(PackRobot packRobot){
        try {
            notificationManager.createNotification(flowerContainer, (notification, operation, list) -> packRobot.tryCreateBouquet(),Operation.WRITE);
            notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> packRobot.tryCreateVegetableBasket(),Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
