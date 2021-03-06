package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MarketServiceImpl extends MarketService {
    final static Logger logger = Logger.getLogger(MarketServiceImpl.class);

    private URI spaceUri;
    private Capi capi;
    private NotificationManager notificationManager;

    private ContainerReference bouquetContainer;
    private ContainerReference vegetableBasketContainer;


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

    public MarketServiceImpl(URI spaceUri) {

        this.spaceUri = spaceUri;

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(), new QueryCoordinator());

        try {
            bouquetContainer = CapiUtil.lookupOrCreateContainer("marketBouquetContainer", spaceUri, coordinators, null,capi);
            vegetableBasketContainer = CapiUtil.lookupOrCreateContainer("marketVegetableBasketContainer", spaceUri, coordinators, null,capi);

            notifications.add(notificationManager.createNotification(bouquetContainer, (notification, operation, list) -> raiseChangedEvent(), Operation.DELETE, Operation.TAKE, Operation.WRITE));
            notifications.add(notificationManager.createNotification(vegetableBasketContainer, (notification, operation, list) -> raiseChangedEvent(), Operation.DELETE, Operation.TAKE, Operation.WRITE));
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    public void putBouquet(Bouquet bouquet, Transaction transaction) {
        ServiceUtil.writeItem(new Entry(bouquet), bouquetContainer, transaction, capi);
    }

    @Override
    public int getAmountOfBouquets() {
        return readAllBouquets().size();
    }

    @Override
    public void putVegetableBasket(VegetableBasket vegetableBasket, Transaction transaction) {
        ServiceUtil.writeItem(new Entry(vegetableBasket), vegetableBasketContainer, transaction, capi);
    }

    @Override
    public List<Bouquet> readAllBouquets() {
        List<Bouquet> bouquets = null;

        try {
            bouquets = capi.read(bouquetContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        return bouquets;
    }

    @Override
    public void sellBouquet(Bouquet bouquet) {

        TransactionReference transaction = null;
        try {
            transaction = capi.createTransaction(100000,spaceUri);

            List<Bouquet> bouquets = takeAllBouquets();

            for (Bouquet b: bouquets) {
                if(!b.equals(bouquet)){
                    putBouquet(b, null);
                }
            }

            capi.commitTransaction(transaction);

        } catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(transaction);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public List<VegetableBasket> readAllVegetableBaskets() {
        List<VegetableBasket> vegetableBaskets = null;

        try {
            vegetableBaskets = capi.read(vegetableBasketContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        return vegetableBaskets;
    }

    @Override
    public void sellVegetableBasket(VegetableBasket vegetableBasket) {

        /*
        Considerations:
            - LindaCoordinator: required Object annotation
            - QueryCoordinator: couldn't find elegant way to use it
        */

        TransactionReference transaction = null;
        try {
            transaction = capi.createTransaction(1000,spaceUri);

            List<VegetableBasket> vegetableBaskets = takeAllVegetableBaskets();

            for (VegetableBasket vb: vegetableBaskets) {
                if(!vb.equals(vegetableBasket)){
                    putVegetableBasket(vb, null);
                }
            }

            capi.commitTransaction(transaction);

        } catch (MzsCoreException e) {
            try {
                capi.rollbackTransaction(transaction);
            } catch (MzsCoreException e1) {
                e1.printStackTrace();
            }
        }

    }

    private List<VegetableBasket> takeAllVegetableBaskets() {
        List<VegetableBasket> vegetableBaskets = null;

        try {
            vegetableBaskets = capi.take(vegetableBasketContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        return vegetableBaskets;
    }

    private List<Bouquet> takeAllBouquets() {
        List<Bouquet> bouquets = null;

        try {
            bouquets = capi.take(bouquetContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        return bouquets;
    }
}
