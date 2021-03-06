package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.net.URI;

public class PackingServiceImpl extends PackingService {
    final static Logger logger = Logger.getLogger(ResearchServiceImpl.class);

    private Capi capi;

    private ContainerReference vegetableContainer;
    private ContainerReference flowerContainer;
    private NotificationManager notificationManager;

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

    public PackingServiceImpl(URI spaceUri) {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(), new FifoCoordinator(), new QueryCoordinator(), new LabelCoordinator());

        try {
            vegetableContainer = CapiUtil.lookupOrCreateContainer("packingVegetableContainer", spaceUri, coordinators, null, capi);
            flowerContainer = CapiUtil.lookupOrCreateContainer("packingFlowerContainer", spaceUri, coordinators, null, capi);

            try {
                notifications.add(notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> raiseVegetablesChanged(), Operation.DELETE, Operation.TAKE, Operation.WRITE));
                notifications.add(notificationManager.createNotification(flowerContainer, (notification, operation, list) -> raiseFlowersChanged(), Operation.DELETE, Operation.TAKE, Operation.WRITE));
            } catch (InterruptedException e) {
                logger.trace("EXCEPTION", e);
            }
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    public void putFlower(Flower flower, Transaction transaction) {
        Entry entry = new Entry(flower, LabelCoordinator.newCoordinationData(flower.getParentFlowerPlant().getTypeName()));
        ServiceUtil.writeItem(entry,flowerContainer,transaction,capi);
    }

    @Override
    public void putVegetable(Vegetable vegetable, Transaction transaction) {
        Entry entry = new Entry(vegetable, LabelCoordinator.newCoordinationData(vegetable.getParentVegetablePlant().getTypeName()));
        ServiceUtil.writeItem(entry,vegetableContainer,transaction,capi);
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

    @Override
    public Vegetable getVegetableByType(VegetableType type, Transaction transaction) {
        return ServiceUtil.getItem(LabelCoordinator.newSelector(type.name(), 1),vegetableContainer,transaction,capi);
    }

    @Override
    public Flower getFlowerByType(FlowerType type, Transaction transaction) {
        return ServiceUtil.getItem(LabelCoordinator.newSelector(type.name(), 1),flowerContainer,transaction,capi);
    }

    public void registerPackRobot(PackRobot packRobot){
        try {
            Notification notificationFlowerContainer = notificationManager.createNotification(flowerContainer, (notification, operation, list) -> packRobot.tryCreateBouquet(),Operation.WRITE);
            Notification notificationVegetableContainer = notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> packRobot.tryCreateVegetableBasket(),Operation.WRITE);

            notifications.add(notificationFlowerContainer);
            notifications.add(notificationVegetableContainer);

        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }
    }
}
