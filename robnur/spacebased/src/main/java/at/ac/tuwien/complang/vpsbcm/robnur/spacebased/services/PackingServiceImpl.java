package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;

import java.util.Arrays;
import java.util.List;
import java.net.URI;

public class PackingServiceImpl implements PackingService {

    Capi capi;

    ContainerReference vegContainer;
    ContainerReference floContainer;

    public PackingServiceImpl(URI spaceUri) throws MzsCoreException {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        List<FifoCoordinator> coords = Arrays.asList(new FifoCoordinator());

        vegContainer = CapiUtil.lookupOrCreateContainer("packingVegetableContainer", spaceUri, coords, null, capi);
        floContainer = CapiUtil.lookupOrCreateContainer("packingFlowerContainer", spaceUri, coords, null, capi);
    }

    @Override
    public void putFlower(Flower flower) {
        try {
            capi.write(floContainer, new Entry(flower));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putVegetable(Vegetable vegetable) {
        try {
            capi.write(vegContainer, new Entry(vegetable));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Flower> getFlowersForBouquet(int amount) {
        List<Flower> flowers = null;

        try {
            flowers = capi.take(floContainer, FifoCoordinator.newSelector(amount), MzsConstants.RequestTimeout.INFINITE, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    @Override
    public List<Vegetable> getVegetableForBasket(int amount) {
        List<Vegetable> vegs = null;

        try {
            vegs = capi.take(vegContainer, FifoCoordinator.newSelector(amount), MzsConstants.RequestTimeout.INFINITE, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegs;
    }

    @Override
    public List<Flower> readAllFlowers() {
        List<Flower> flowers = null;

        try {
            flowers = capi.read(floContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.INFINITE, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    @Override
    public List<Vegetable> readAllVegetables() {
        List<Vegetable> vegs = null;

        try {
            vegs = capi.read(vegContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.INFINITE, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegs;
    }
}
