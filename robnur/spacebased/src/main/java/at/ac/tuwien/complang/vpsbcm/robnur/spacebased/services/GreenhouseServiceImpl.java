package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GreenhouseServiceImpl extends GreenhouseService {

    private static final String FLOWER_LABEL = "flower";
    private static final String VEGETABLE_LABEL = "vegetable";

    private URI spaceUri;
    private Capi capi;
    private NotificationManager notificationManager;

    private ContainerReference greenhouseContainer;

    private void raiseChangedEvent() {
        if(greenhouseChanged != null) {
            List<Plant> pants = new LinkedList<>();
            pants.addAll(readAllFlowerPlants());
            pants.addAll(readAllVegetablePlants());

            greenhouseChanged.handle(pants);
        }
    }

    public GreenhouseServiceImpl(URI spaceUri) throws MzsCoreException, InterruptedException {
        this.spaceUri = spaceUri;

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new LabelCoordinator(), new QueryCoordinator());

        String greenhouseContainerName = "greenhouseContainer";

        try {
            greenhouseContainer = capi.lookupContainer(greenhouseContainerName, spaceUri, MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (ContainerNotFoundException var9) {
            try {
                greenhouseContainer = capi.createContainer(greenhouseContainerName, spaceUri, 20, coordinators, null, null); // create bounded container
            } catch (ContainerNameNotAvailableException var8) {
                greenhouseContainer = capi.lookupContainer(greenhouseContainerName, spaceUri, MzsConstants.RequestTimeout.DEFAULT, null);
            }
        }

        notificationManager.createNotification(greenhouseContainer, (notification, operation, list) -> raiseChangedEvent(), Operation.DELETE, Operation.TAKE, Operation.WRITE);
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {
        try {
            notificationManager.createNotification(greenhouseContainer, (notification, operation, list) -> { robot.tryHarvestPlant(); robot.tryPlant();}, Operation.WRITE, Operation.TAKE, Operation.DELETE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void plant(VegetablePlant vegetablePlant, Transaction transaction) {
        TransactionReference ref = TransactionServiceImpl.getTransactionReference(transaction);

        Entry entry = new Entry(vegetablePlant, LabelCoordinator.newCoordinationData(VEGETABLE_LABEL));
        try {
            capi.write(greenhouseContainer, MzsConstants.RequestTimeout.DEFAULT, ref, entry);
        }
        catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void plant(FlowerPlant flowerPlant, Transaction transaction) {
        TransactionReference ref = TransactionServiceImpl.getTransactionReference(transaction);

        Entry entry = new Entry(flowerPlant, LabelCoordinator.newCoordinationData(FLOWER_LABEL));
        try {
            capi.write(greenhouseContainer, MzsConstants.RequestTimeout.DEFAULT, ref, entry);
        }
        catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<VegetablePlant> getAllVegetablePlants(Transaction transaction) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector(VEGETABLE_LABEL, MzsConstants.Selecting.COUNT_MAX)
            );

            ArrayList<VegetablePlant> vegetablePlants = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, transactionReference);

            return vegetablePlants;
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<FlowerPlant> getAllFlowerPlants(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector(FLOWER_LABEL, MzsConstants.Selecting.COUNT_MAX)
            );

            ArrayList<FlowerPlant> ps = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, tref);
            return ps;
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<VegetablePlant> vegetablePlants = null;
        try {
            vegetablePlants = capi.read(greenhouseContainer, LabelCoordinator.newSelector(VEGETABLE_LABEL,LabelCoordinator.LabelSelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return vegetablePlants;
    }

    @Override
    public List<FlowerPlant> readAllFlowerPlants(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<FlowerPlant> flowerPlants = null;
        try {
            flowerPlants = capi.read(greenhouseContainer, LabelCoordinator.newSelector(FLOWER_LABEL,LabelCoordinator.LabelSelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,tref);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return flowerPlants;
    }


    @Override
    protected VegetablePlant getHarvestableVegetablePlant(Transaction t) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(t);

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector(VEGETABLE_LABEL, MzsConstants.Selecting.COUNT_MAX),
                    QueryCoordinator.newSelector(query.filter(growthProperty.greaterThanOrEqualTo(100)).cnt(0,1), MzsConstants.Selecting.COUNT_MAX)
            );

            ArrayList<VegetablePlant> vegetablePlants = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, transactionReference);

            if(vegetablePlants.size() > 0) {
                return vegetablePlants.get(0);
            }
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected FlowerPlant getHarvestableFlowerPlant(Transaction t) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(t);

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector(FLOWER_LABEL, MzsConstants.Selecting.COUNT_MAX),
                    QueryCoordinator.newSelector(query.filter(growthProperty.greaterThanOrEqualTo(100)).cnt(0,1), MzsConstants.Selecting.COUNT_MAX)
            );

            ArrayList<FlowerPlant> ps = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, tref);
            if(ps.size() > 0) {
                return ps.get(0);

            }
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }

}
