package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class StorageServiceImpl extends StorageService {

    final static Logger logger = Logger.getLogger(StorageServiceImpl.class);

    private Capi capi;
    private NotificationManager notificationManager;

    private ContainerReference flowerSeedContainer;
    private ContainerReference vegetableSeedContainer;

    private ContainerReference soilContainer;
    private ContainerReference flowerFertilizerContainer;
    private ContainerReference vegetableFertilizerContainer;
    private ContainerReference waterContainer;
    private ContainerReference waterTokenContainer;
    private ContainerReference waterAccessContainer;
    private URI spaceUri;

    List<Notification> notifications = new LinkedList<>();

    boolean exit = false;

    @Override
    public boolean isExit() {
        return exit;
    }

    @Override
    public void setExit(boolean exit) {
        this.exit = exit;
        if(exit == true) {
            for(Notification n : notifications) {
                try {
                    n.destroy();
                } catch (MzsCoreException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public StorageServiceImpl(URI spaceUri) throws MzsCoreException, URISyntaxException {

        this.spaceUri = spaceUri;

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();

        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<FifoCoordinator> coords = null;

        flowerSeedContainer = CapiUtil.lookupOrCreateContainer("storageFlowerSeedContainer", spaceUri, Arrays.asList(new AnyCoordinator(), new QueryCoordinator(), new LabelCoordinator()), null, capi);
        vegetableSeedContainer = CapiUtil.lookupOrCreateContainer("storageVegetableSeedContainer", spaceUri, Arrays.asList(new AnyCoordinator(), new QueryCoordinator(), new LabelCoordinator()), null, capi);

        soilContainer = CapiUtil.lookupOrCreateContainer("storageSoilContainer", spaceUri, coords, null, capi);
        flowerFertilizerContainer = CapiUtil.lookupOrCreateContainer("storageFlowerFertilizerContainer", spaceUri, coords, null, capi);
        vegetableFertilizerContainer = CapiUtil.lookupOrCreateContainer("storageVegetableFertilizerContainer", spaceUri, coords, null, capi);
        waterContainer = CapiUtil.lookupOrCreateContainer("waterContainer", spaceUri, Arrays.asList(new AnyCoordinator()), null, capi);
        waterTokenContainer = CapiUtil.lookupOrCreateContainer("waterTokenContainer", spaceUri, Arrays.asList(new AnyCoordinator()), null, capi);
        waterAccessContainer = CapiUtil.lookupOrCreateContainer("waterAccessContainer", spaceUri, Arrays.asList(new AnyCoordinator()), null, capi);

        try {
            notifications.add(notificationManager.createNotification(flowerSeedContainer, (notification, operation, list) -> notifyFlowerSeedsChanged(readAllFlowerSeeds()), Operation.DELETE, Operation.TAKE, Operation.WRITE));
            notifications.add(notificationManager.createNotification(vegetableSeedContainer, (notification, operation, list) -> notifyVegetableSeedsChanged(readAllVegetableSeeds()), Operation.DELETE, Operation.TAKE, Operation.WRITE));

            notifications.add(notificationManager.createNotification(soilContainer, (notification, operation, list) -> notifySoilPackagesChanged(readAllSoilPackage()), Operation.DELETE, Operation.TAKE, Operation.WRITE));
            notifications.add(notificationManager.createNotification(flowerFertilizerContainer, (notification, operation, list) -> notifyFlowerFertilizerChanged(readAllFlowerFertilizer()), Operation.DELETE, Operation.TAKE, Operation.WRITE));
            notifications.add(notificationManager.createNotification(vegetableFertilizerContainer, (notification, operation, list) -> notifyVegetableFertilizerChanged(readAllVegetableFertilizer()), Operation.DELETE, Operation.TAKE, Operation.WRITE));

            notifications.add(notificationManager.createNotification(waterAccessContainer, (notification, operation, list) -> {
                if(operation == Operation.WRITE) {
                    Entry e = (Entry)list.get(0);
                    notifyWaterRobotChanged((String)e.getValue());
                } else if (operation == Operation.TAKE) {
                    notifyWaterRobotChanged(null);
                }
            }, Operation.TAKE, Operation.WRITE));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {
        try {
            Notification notificationFlowerSeedContainer = notificationManager.createNotification(flowerSeedContainer, (notification, operation, list) -> {
                logger.debug("at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot notify - got notification on flowerSeedContainer");
                robot.tryHarvestPlant();
                robot.tryPlant();
            }, Operation.WRITE);

            Notification notificationVegetableSeedContainer = notificationManager.createNotification(vegetableSeedContainer, (notification, operation, list) -> {
                logger.debug("at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot notify - got notification on vegetableSeedContainer");
                robot.tryHarvestPlant();
                robot.tryPlant();
            }, Operation.WRITE);

            Notification notificationSoilContainer = notificationManager.createNotification(soilContainer, (notification, operation, list) -> {
                logger.debug("at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot notify - got notification on soilContainer");
                robot.tryHarvestPlant();
                robot.tryPlant();
            }, Operation.WRITE);

            Notification notificationFlowerFertilizerContainer = notificationManager.createNotification(flowerFertilizerContainer, (notification, operation, list) -> {
                logger.debug("at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot notify - got notification on flowerFertilizerContainer");
                robot.tryHarvestPlant();
                robot.tryPlant();
            }, Operation.WRITE);

            Notification notificationVegetableFertilizerContainer = notificationManager.createNotification(vegetableFertilizerContainer, (notification, operation, list) -> {
                logger.debug("at.ac.tuwien.complang.vpsbcm.robnur.postgres.robot notify - got notification on vegetableFertilizerContainer");
                robot.tryHarvestPlant();
                robot.tryPlant();
            }, Operation.WRITE);

            notifications.add(notificationFlowerSeedContainer);
            notifications.add(notificationVegetableSeedContainer);
            notifications.add(notificationSoilContainer);
            notifications.add(notificationFlowerFertilizerContainer);
            notifications.add(notificationVegetableFertilizerContainer);

            return notifications;
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void putSeed(VegetablePlant plant, Transaction transaction) {
        putVegetableSeeds(Collections.singletonList(plant), transaction);
    }

    @Override
    public void putVegetableSeeds(List<VegetablePlant> plants, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            List<Entry> entries = new LinkedList<>();
            for(VegetablePlant plant : plants) {
                entries.add(new Entry(plant, LabelCoordinator.newCoordinationData(plant.getTypeName())));
            }
            capi.write(entries, vegetableSeedContainer, MzsConstants.RequestTimeout.INFINITE, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFlowerSeeds(List<FlowerPlant> plants, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            List<Entry> entries = new LinkedList<>();
            logger.info("put " + plants.size() + "flower seeds into storage");
            for(FlowerPlant plant : plants) {
                entries.add(new Entry(plant, LabelCoordinator.newCoordinationData(plant.getTypeName())));
            }
            capi.write(entries, flowerSeedContainer, MzsConstants.RequestTimeout.INFINITE, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected FlowerPlant getSeed(FlowerType type, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        FlowerPlant plant = null;

        try {
            ArrayList<FlowerPlant> plants = capi.take(flowerSeedContainer, Collections.singletonList(LabelCoordinator.newSelector(type.name())), MzsConstants.RequestTimeout.DEFAULT, tref);
            if(plants != null && !plants.isEmpty()) {
                plant = plants.get(0);
            }
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return plant;
    }

    @Override
    protected VegetablePlant getSeed(VegetableType type, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        VegetablePlant plant = null;

        try {
            ArrayList<VegetablePlant> plants = capi.take(vegetableSeedContainer, Collections.singletonList(LabelCoordinator.newSelector(type.name())), MzsConstants.RequestTimeout.DEFAULT, tref);
            if(plants != null && !plants.isEmpty()) {
                plant = plants.get(0);
            }
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return plant;
    }

    @Override
    public void putSeed(FlowerPlant plant, Transaction transaction) {
        putFlowerSeeds(Collections.singletonList(plant), transaction);
    }

    @Override
    public List<FlowerPlant> readAllFlowerSeeds(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<FlowerPlant> plants = null;

        try {
            plants = capi.read(flowerSeedContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), MzsConstants.RequestTimeout.ZERO, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
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
            plants = capi.read(vegetableSeedContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), MzsConstants.RequestTimeout.ZERO, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
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
            soilPackages = capi.take(soilContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), MzsConstants.RequestTimeout.DEFAULT, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return soilPackages;
    }

    @Override
    public void putSoilPackage(SoilPackage soilPackage, Transaction transaction) {
        putSoilPackages(Collections.singletonList(soilPackage), transaction);
    }

    @Override
    public void putSoilPackages(List<SoilPackage> soilPackages, Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            List<Entry> entries = new LinkedList<>();
            for(SoilPackage p : soilPackages) {
                entries.add(new Entry(p));
            }

            capi.write(entries, soilContainer, MzsConstants.RequestTimeout.INFINITE, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SoilPackage> readAllSoilPackage(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        ArrayList<SoilPackage> packages = null;

        try {
            packages = capi.read(soilContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), MzsConstants.RequestTimeout.ZERO, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
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
            flowerFertilizers = capi.take(flowerFertilizerContainer, AnyCoordinator.newSelector(amount), MzsConstants.RequestTimeout.DEFAULT, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowerFertilizers;
    }


    @Override
    public void putFlowerFertilizer(FlowerFertilizer flowerFertilizer) {
        putFlowerFertilizers(Collections.singletonList(flowerFertilizer));
    }

    @Override
    public void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers) {
        putFlowerFertilizers(flowerFertilizers, null);
    }

    @Override
    public void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers, Transaction t) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(t);

        try {
            List<Entry> entries = new LinkedList<>();
            for(FlowerFertilizer flowerFertilizer : flowerFertilizers) {
                entries.add(new Entry(flowerFertilizer));
            }

            capi.write(entries, flowerFertilizerContainer, MzsConstants.RequestTimeout.INFINITE, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(t);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FlowerFertilizer> readAllFlowerFertilizer(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<FlowerFertilizer> flowerFertilizers = null;

        try {
            flowerFertilizers = capi.read(flowerFertilizerContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), MzsConstants.RequestTimeout.ZERO, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
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
            vegetableFertilizers = capi.take(vegetableFertilizerContainer, AnyCoordinator.newSelector(amount), MzsConstants.RequestTimeout.DEFAULT, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetableFertilizers;
    }

    @Override
    public void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer) {
        putVegetableFertilizers(Collections.singletonList(vegetableFertilizer));
    }

    @Override
    public void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers) {
        putVegetableFertilizers(vegetableFertilizers, null);
    }

    @Override
    public void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers, Transaction t) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(t);

        try {
            List<Entry> entries = new LinkedList<>();
            for(VegetableFertilizer fertilizer : vegetableFertilizers) {
                entries.add(new Entry(fertilizer));
            }

            capi.write(entries, vegetableFertilizerContainer, MzsConstants.RequestTimeout.INFINITE, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(t);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<VegetableFertilizer> readAllVegetableFertilizer(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<VegetableFertilizer> vegetableFertilizers = null;

        try {
            vegetableFertilizers = capi.read(vegetableFertilizerContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), MzsConstants.RequestTimeout.ZERO, tref);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
            e.printStackTrace();
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetableFertilizers;
    }

    public void putWater(Water water) {
        try {
            capi.write(waterContainer, new Entry(water));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Water accessTap(String robotId) {
        logger.info("in accessTap");

        try {
            logger.info(robotId + " wait for water");
            List<String> tokens = capi.take(waterTokenContainer,AnyCoordinator.newSelector(),MzsConstants.RequestTimeout.INFINITE,null);
            logger.info(robotId + " got water");

            if(tokens == null || tokens.isEmpty()){
                logger.fatal("WaterFATAL no token");
                return null;
            }else if(tokens.size() > 1){
                logger.fatal("WaterFATAL to many tokens");
                return null;
            }

            logger.info(robotId + " write name into waterAccessContainer");
            capi.write(new Entry(robotId),waterAccessContainer,MzsConstants.RequestTimeout.INFINITE,null);

            logger.info(robotId + " wait for water");
            Thread.sleep(1000);
            Water water = new Water();
            water.setAmount(250);
            logger.info(robotId + " create water");


            logger.info(robotId + " remove name");
            capi.take(waterAccessContainer,AnyCoordinator.newSelector(),MzsConstants.RequestTimeout.INFINITE,null);

            logger.info(robotId + " put back token");
            capi.write(new Entry(tokens.get(0)),waterTokenContainer,MzsConstants.RequestTimeout.INFINITE,null);

            logger.info(robotId + " return water");


            return water;

        } catch (MzsCoreException e) {
            logger.info("MzsCoreException accessTap");

            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.info("InterruptedException accessTap");

            e.printStackTrace();
        }

        return null;
    }
}
