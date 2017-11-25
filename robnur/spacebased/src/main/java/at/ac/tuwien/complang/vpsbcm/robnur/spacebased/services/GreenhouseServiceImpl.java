package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreenhouseServiceImpl implements GreenhouseService {

    private URI spaceUri;
    private Capi capi;
    private NotificationManager notificationManager;

    private ContainerReference greenhouseContainer;

    public GreenhouseServiceImpl(URI spaceUri) throws MzsCoreException {
        this.spaceUri = spaceUri;

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coords = Arrays.asList(new LabelCoordinator(), new QueryCoordinator());

        String greenhouseContainerName="greenhouseContainer";
        try {
            greenhouseContainer = capi.lookupContainer(greenhouseContainerName, spaceUri, -2L, null);
        } catch (ContainerNotFoundException var9) {
            try {
                greenhouseContainer = capi.createContainer(greenhouseContainerName, spaceUri, 20, coords, (List)null, null); // create bounded container
            } catch (ContainerNameNotAvailableException var8) {
                greenhouseContainer = capi.lookupContainer(greenhouseContainerName, spaceUri, -2L, null);
            }
        }
    }

    @Override
    public void plant(VegetablePlant veg, Transaction t) {
        TransactionReference ref = TransactionServiceImpl.getTransactionReference(t);

        Entry entry = new Entry(veg, LabelCoordinator.newCoordinationData("veg"));
        try {
            capi.write(greenhouseContainer, MzsConstants.RequestTimeout.DEFAULT, ref, entry);
        }
        catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void plant(FlowerPlant plant, Transaction t) {
        TransactionReference ref = TransactionServiceImpl.getTransactionReference(t);

        Entry entry = new Entry(plant, LabelCoordinator.newCoordinationData("flo"));
        try {
            capi.write(greenhouseContainer, MzsConstants.RequestTimeout.DEFAULT, ref, entry);
        }
        catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Vegetable> harvestVegetablePlant(Transaction t) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(t);
        List<Vegetable> vegs = null;

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector("veg"),
                    QueryCoordinator.newSelector(query.filter(growthProperty.greaterThanOrEqualTo(100)).cnt(1))
            );

            ArrayList<VegetablePlant> ps = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, tref);
            VegetablePlant plant = ps.get(0);

            vegs = Vegetable.fromVegetablePlant(plant);

            // If this plant can still be harvested
            if(plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
                this.plant(plant, t);
            }
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegs;
    }

    @Override
    public List<Flower> harvestFlowerPlant(Transaction t) {
        TransactionReference tref = TransactionServiceImpl.getTransactionReference(t);
        List<Flower> flowers = null;

        try {
            ComparableProperty growthProperty = ComparableProperty.forName("growth");
            Query query = new Query();

            List<Selector> selectors = Arrays.asList(
                    LabelCoordinator.newSelector("flo"),
                    QueryCoordinator.newSelector(query.filter(growthProperty.greaterThanOrEqualTo(100)).cnt(1))
            );

            ArrayList<FlowerPlant> ps = capi.take(greenhouseContainer, selectors , MzsConstants.RequestTimeout.DEFAULT, tref);
            FlowerPlant plant = ps.get(0);

            flowers = Flower.fromFlowerPlant(plant);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    @Override
    public VegetablePlant readAllVegetablePlants() {
        return null;
    }

    @Override
    public FlowerPlant readAllFlowerPlants() {
        return null;
    }
}
