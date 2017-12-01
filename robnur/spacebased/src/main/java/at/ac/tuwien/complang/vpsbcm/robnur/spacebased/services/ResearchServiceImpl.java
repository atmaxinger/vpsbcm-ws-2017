package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
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
        ServiceUtil.writeItem(flower,flowerContainer,null,capi);
    }

    @Override
    public void putVegetable(Vegetable vegetable) {
        ServiceUtil.writeItem(vegetable,vegetableContainer,null,capi);
    }

    @Override
    public void deleteFlower(Flower flower, Transaction transaction) {
        ServiceUtil.deleteItemById(flower.getId(),flowerContainer,transaction,capi);
    }

    @Override
    public void deleteVegetable(Vegetable vegetable,Transaction transaction) {
        ServiceUtil.deleteItemById(vegetable.getId(),flowerContainer,transaction,capi);
    }

    @Override
    public List<Flower> readAllFlowers(Transaction transaction) {
        return ServiceUtil.readAllItems(flowerContainer,transaction,capi);
    }

    @Override
    public List<Vegetable> readAllVegetables(Transaction transaction) {
        return ServiceUtil.readAllItems(vegetableContainer,transaction,capi);
    }
}