package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class CompostServiceImpl implements CompostService {

    Capi capi;

    ContainerReference flowerPlantContainer;
    ContainerReference flowerContainer;
    ContainerReference vegetablePlantContainer;
    ContainerReference vegetableContainer;

    public CompostServiceImpl(URI spaceUri) {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator());

        try {
            flowerPlantContainer = CapiUtil.lookupOrCreateContainer("flowerPlantContainer",spaceUri,coordinators,null,capi);
            flowerContainer = CapiUtil.lookupOrCreateContainer("flowerContainer",spaceUri,coordinators,null,capi);
            vegetablePlantContainer = CapiUtil.lookupOrCreateContainer("vegetablePlantContainer",spaceUri,coordinators,null,capi);
            vegetableContainer = CapiUtil.lookupOrCreateContainer("vegetableContainer",spaceUri,coordinators,null,capi);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFlowerPlant(FlowerPlant flowerPlant) {
        try {
            capi.write(flowerPlantContainer,new Entry(flowerPlant));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putVegetablePlant(VegetablePlant vegetablePlant) {
        try {
            capi.write(vegetablePlantContainer,new Entry(vegetablePlant));
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
    public List<FlowerPlant> readAllFlowerPlants() {

        List<FlowerPlant> flowerPlants = null;

        try {
            flowerPlants = capi.read(flowerPlantContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowerPlants;
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants() {

        List<VegetablePlant> vegetablePlants = null;

        try {
            vegetablePlants = capi.read(vegetablePlantContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX),MzsConstants.RequestTimeout.DEFAULT,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetablePlants;
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
