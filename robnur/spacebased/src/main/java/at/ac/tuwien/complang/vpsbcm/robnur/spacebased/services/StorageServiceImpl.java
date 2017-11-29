package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.ContainerIPoint;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import javax.transaction.TransactionRequiredException;
import java.awt.*;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StorageServiceImpl extends StorageService {

    private Capi capi;
    private NotificationManager notificationManager;

    private ContainerReference flowerSeedContainer;
    private ContainerReference vegetableSeedContainer;

    private ContainerReference soilContainer;
    private ContainerReference flowerFertilizerContainer;
    private ContainerReference vegetableFertilizerContainer;
    private ContainerReference waterContainer;

    public StorageServiceImpl(URI spaceUri) throws MzsCoreException, URISyntaxException {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();

        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<FifoCoordinator> coords = null;//Arrays.asList(new FifoCoordinator());

        flowerSeedContainer = CapiUtil.lookupOrCreateContainer("flowerSeedContainer",spaceUri, Arrays.asList(new AnyCoordinator(), new QueryCoordinator()),null,capi);
        vegetableSeedContainer = CapiUtil.lookupOrCreateContainer("vegetableSeedContainer",spaceUri, Arrays.asList(new AnyCoordinator(), new QueryCoordinator()),null,capi);

        soilContainer = CapiUtil.lookupOrCreateContainer("soilContainer",spaceUri,coords,null,capi);
        flowerFertilizerContainer = CapiUtil.lookupOrCreateContainer("flowerFertilizerContainer",spaceUri,coords,null,capi);
        vegetableFertilizerContainer = CapiUtil.lookupOrCreateContainer("vegetableFertilizerContainer",spaceUri,coords,null,capi);
        waterContainer = CapiUtil.lookupOrCreateContainer("waterContainer",spaceUri,coords,null,capi);

        try {
            notificationManager.createNotification(flowerSeedContainer, (notification, operation, list) -> flowerSeedsChanged.handle(readAllFlowerSeeds()), Operation.DELETE, Operation.TAKE, Operation.WRITE);
            notificationManager.createNotification(vegetableSeedContainer, (notification, operation, list) -> vegetableSeedChanged.handle(readAllVegetableSeeds()), Operation.DELETE, Operation.TAKE, Operation.WRITE);

            notificationManager.createNotification(soilContainer, (notification, operation, list) -> soilPackagesChanged.handle(readAllSoilPackage()), Operation.DELETE, Operation.TAKE, Operation.WRITE);
            notificationManager.createNotification(flowerFertilizerContainer, (notification, operation, list) -> flowerFertilizerChanged.handle(readAllFlowerFertilizer()), Operation.DELETE, Operation.TAKE, Operation.WRITE);
            notificationManager.createNotification(vegetableFertilizerContainer, (notification, operation, list) -> vegetableFertilizerChanged.handle(readAllVegetableFertilizer()), Operation.DELETE, Operation.TAKE, Operation.WRITE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {
        try {
            notificationManager.createNotification(flowerSeedContainer, (notification, operation, list) -> { robot.tryHarvestPlant(); robot.tryPlant(); }, Operation.WRITE);
            notificationManager.createNotification(vegetableSeedContainer, (notification, operation, list) -> { robot.tryHarvestPlant(); robot.tryPlant(); },  Operation.WRITE);

            notificationManager.createNotification(soilContainer, (notification, operation, list) -> { robot.tryHarvestPlant(); robot.tryPlant(); }, Operation.WRITE);
            notificationManager.createNotification(flowerFertilizerContainer, (notification, operation, list) -> { robot.tryHarvestPlant(); robot.tryPlant(); }, Operation.WRITE);
            notificationManager.createNotification(vegetableFertilizerContainer, (notification, operation, list) -> { robot.tryHarvestPlant(); robot.tryPlant(); },  Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putSeed(VegetablePlant plant, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(vegetableSeedContainer, MzsConstants.RequestTimeout.INFINITE, tref, new Entry(plant));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void deleteSeed(VegetablePlant plant, Transaction transaction) {
        ServiceUtil.deleteItemById(plant.getId(), vegetableSeedContainer, transaction, capi);
    }

    @Override
    protected void deleteSeed(FlowerPlant plant, Transaction transaction) {
        ServiceUtil.deleteItemById(plant.getId(), flowerSeedContainer, transaction, capi);
    }

    @Override
    public void putSeed(FlowerPlant plant, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(flowerSeedContainer, MzsConstants.RequestTimeout.INFINITE, tref, new Entry(plant));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FlowerPlant> readAllFlowerSeeds(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<FlowerPlant> plants = null;

        try {
            plants = capi.read(flowerSeedContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.ZERO,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return plants;
    }

    @Override
    public List<VegetablePlant> readAllVegetableSeeds(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<VegetablePlant> plants = null;

        try {
            plants = capi.read(vegetableSeedContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.ZERO,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return plants;
    }


    @Override
    protected List<SoilPackage> getAllSoilPackages(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        ArrayList<SoilPackage> soilPackages = null;

        try {
            soilPackages = capi.take(soilContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.INFINITE,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return soilPackages;
    }

    @Override
    public void putSoilPackage(SoilPackage soilPackage, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(soilContainer, MzsConstants.RequestTimeout.DEFAULT, tref, new Entry(soilPackage));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SoilPackage> readAllSoilPackage(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        ArrayList<SoilPackage> packages = null;

        try {
            packages = capi.read(soilContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.ZERO, tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return packages;
    }

    @Override
    public List<FlowerFertilizer> getFlowerFertilizer(int amount, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        ArrayList<FlowerFertilizer> flowerFertilizers = null;

        try {
             flowerFertilizers = capi.take(flowerFertilizerContainer, AnyCoordinator.newSelector(amount),MzsConstants.RequestTimeout.INFINITE,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowerFertilizers;
    }


    @Override
    public void putFlowerFertilizer(FlowerFertilizer flowerFertilizer) {
        try {
            capi.write(flowerFertilizerContainer, new Entry(flowerFertilizer));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FlowerFertilizer> readAllFlowerFertilizer(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<FlowerFertilizer> flowerFertilizers = null;

        try {
            flowerFertilizers = capi.read(flowerFertilizerContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.ZERO,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowerFertilizers;
    }

    @Override
    public List<VegetableFertilizer> getVegetableFertilizer(int amount, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        ArrayList<VegetableFertilizer> vegetableFertilizers = null;

        try {
            vegetableFertilizers = capi.take(vegetableFertilizerContainer, AnyCoordinator.newSelector(amount),MzsConstants.RequestTimeout.INFINITE,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetableFertilizers;
    }

    @Override
    public void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer) {
        try {
            capi.write(vegetableFertilizerContainer,new Entry(vegetableFertilizer));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<VegetableFertilizer> readAllVegetableFertilizer(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<VegetableFertilizer> vegetableFertilizers = null;

        try {
            vegetableFertilizers = capi.read(vegetableFertilizerContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.ZERO,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetableFertilizers;
    }

    public void putWater(Water water){
        try {
            capi.write(waterContainer,new Entry(water));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    public Water accessTap() {
        ArrayList<Water> waterArrayList = null;

        try {
            waterArrayList = capi.take(waterContainer,AnyCoordinator.newSelector(),MzsConstants.RequestTimeout.INFINITE,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return waterArrayList.get(0);
    }
}
