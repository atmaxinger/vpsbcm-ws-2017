package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PackRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.net.URI;

public class PackingServiceImpl implements PackingService {

    Capi capi;

    ContainerReference vegetableContainer;
    ContainerReference flowerContainer;
    NotificationManager notificationManager;

    public PackingServiceImpl(URI spaceUri) throws MzsCoreException {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new FifoCoordinator(), new QueryCoordinator());

        vegetableContainer = CapiUtil.lookupOrCreateContainer("packingVegetableContainer", spaceUri, coordinators, null, capi);
        flowerContainer = CapiUtil.lookupOrCreateContainer("packingFlowerContainer", spaceUri, coordinators, null, capi);
    }

    @Override
    public void putFlower(Flower flower) {
        try {
            capi.write(flowerContainer, new Entry(flower));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putVegetable(Vegetable vegetable) {
        try {
            capi.write(vegetableContainer, new Entry(vegetable));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Flower getFlower(String flowerId, Transaction transaction) {

        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        Query query = new Query();

        ComparableProperty idProperty = ComparableProperty.forName("id");
        List<Selector> selectors = Arrays.asList(QueryCoordinator.newSelector(query.filter(idProperty.matches(flowerId)))); // TODO: check if that actually works

        List<Flower> flowers = null;

        try {
            flowers = capi.take(flowerContainer,selectors,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers.get(0);
    }

    @Override
    public Vegetable getVegetable(String vegetableId, Transaction transaction) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        Query query = new Query();

        ComparableProperty idProperty = ComparableProperty.forName("id");
        List<Selector> selectors = Arrays.asList(QueryCoordinator.newSelector(query.filter(idProperty.matches(vegetableId)))); // TODO: check if that actually works

        List<Vegetable> vegetables = null;

        try {
            vegetables = capi.take(vegetableContainer,selectors,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetables.get(0);
    }

    @Override
    public List<Flower> readAllFlowers(Transaction transaction) {

        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<Flower> flowers = null;

        try {
            flowers = capi.read(flowerContainer, FifoCoordinator.newSelector(FifoCoordinator.FifoSelector.COUNT_MAX), MzsConstants.RequestTimeout.INFINITE, transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    @Override
    public List<Vegetable> readAllVegetables(Transaction transaction) {

        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<Vegetable> vegetables = null;

        try {
            vegetables = capi.read(vegetableContainer, FifoCoordinator.newSelector(FifoCoordinator.FifoSelector.COUNT_MAX), MzsConstants.RequestTimeout.INFINITE, transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetables;
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
