package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ResearchServiceImpl implements ResearchService {

    Capi capi;

    ContainerReference flowerContainer;
    ContainerReference vegetableContainer;

    public ResearchServiceImpl(URI spaceUri) {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator());

        try {
            flowerContainer = CapiUtil.lookupOrCreateContainer("flowerContainer",spaceUri,coordinators,null,capi);
            vegetableContainer = CapiUtil.lookupOrCreateContainer("vegetableContainer",spaceUri,coordinators,null,capi);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFlower(Flower flower) {
        try {
            capi.write(flowerContainer,new Entry(flower));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putVegetable(Vegetable vegetable) {
        try {
            capi.write(vegetableContainer,new Entry(vegetable));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Flower> readAllFlowers() {

        List<Flower> flowers = null;

        try {
            flowers = capi.read(flowerContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowers;
    }

    @Override
    public List<Vegetable> readAllVegetables() {

        List<Vegetable> vegetables = null;

        try {
            vegetables = capi.read(vegetableContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetables;
    }
}
