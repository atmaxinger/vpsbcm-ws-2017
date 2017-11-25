package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreenhouseServiceImpl implements GreenhouseService {

    private static final String FLOWER_LABEL = "flower";
    private static final String VEGETABLE_LABEL = "vegetable";

    private URI spaceUri;
    private Capi capi;
    private NotificationManager notificationManager;

    private ContainerReference greenhouseContainer;

    public GreenhouseServiceImpl(URI spaceUri) throws MzsCoreException {
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
    public List<Vegetable> harvestVegetablePlant(Transaction transaction) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);
        List<Vegetable> vegetables = null;

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector(VEGETABLE_LABEL, MzsConstants.Selecting.COUNT_MAX),
                    QueryCoordinator.newSelector(query.filter(growthProperty.greaterThanOrEqualTo(100)), MzsConstants.Selecting.COUNT_MAX)
            );

            ArrayList<VegetablePlant> vegetablePlants = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, transactionReference);

            if(vegetablePlants.size() > 0) {
                VegetablePlant plant = vegetablePlants.get(0);

                vegetables = Vegetable.harvestVegetablesFormPlant(plant);

                // if this plant can still be harvested then "plant" it again
                if (plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
                    this.plant(plant, transaction);
                }
            }

        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetables;
    }

    @Override
    public List<Flower> harvestFlowerPlant(Transaction transaction) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(transaction);
        List<Flower> flowers = null;

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector(FLOWER_LABEL, MzsConstants.Selecting.COUNT_MAX),
                    QueryCoordinator.newSelector(query.filter(growthProperty.greaterThanOrEqualTo(100)), MzsConstants.Selecting.COUNT_MAX)
            );

            ArrayList<FlowerPlant> ps = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, tref);
            if(ps.size() > 0) {
                FlowerPlant plant = ps.get(0);
                flowers = Flower.fromFlowerPlant(plant);
            }
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants() {
        List<VegetablePlant> vegetablePlants = null;
        try {
            vegetablePlants = capi.read(greenhouseContainer, LabelCoordinator.newSelector(VEGETABLE_LABEL,LabelCoordinator.LabelSelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return vegetablePlants;
    }

    @Override
    public List<FlowerPlant> readAllFlowerPlants() {
        List<FlowerPlant> flowerPlants = null;
        try {
            flowerPlants = capi.read(greenhouseContainer, LabelCoordinator.newSelector(FLOWER_LABEL,LabelCoordinator.LabelSelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return flowerPlants;
    }
}
