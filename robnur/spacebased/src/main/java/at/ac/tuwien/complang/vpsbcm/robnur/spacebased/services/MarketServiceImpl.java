package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarketServiceImpl implements MarketService {

    private URI spaceUri;
    private Capi capi;

    private ContainerReference bouquetContainer;
    private ContainerReference vegetableBasketContainer;


    public MarketServiceImpl(URI spaceUri) {

        this.spaceUri = spaceUri;

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(), new QueryCoordinator());

        try {
            bouquetContainer = CapiUtil.lookupOrCreateContainer("bouquetContainer", spaceUri, coordinators, null,capi);
            vegetableBasketContainer = CapiUtil.lookupOrCreateContainer("vegetableBasketContainer", spaceUri, coordinators, null,capi);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putBouquet(Bouquet bouquet) {
        try {
            capi.write(bouquetContainer,new Entry(bouquet));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAmountOfBouquets() {
        return readAllBouquets().size();
    }

    @Override
    public void putVegetableBasket(VegetableBasket vegetableBasket) {
        try {
            capi.write(vegetableBasketContainer,new Entry(vegetableBasket));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAmountOfVegetableBaskets() {
        return readAllVegetableBaskets().size();
    }

    @Override
    public List<Bouquet> readAllBouquets() {
        List<Bouquet> bouquets = null;

        try {
            bouquets = capi.read(bouquetContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
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
                    putBouquet(b);
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
            e.printStackTrace();
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
                    putVegetableBasket(vb);
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
            e.printStackTrace();
        }

        return vegetableBaskets;
    }

    private List<Bouquet> takeAllBouquets() {
        List<Bouquet> bouquets = null;

        try {
            bouquets = capi.take(bouquetContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return bouquets;
    }
}