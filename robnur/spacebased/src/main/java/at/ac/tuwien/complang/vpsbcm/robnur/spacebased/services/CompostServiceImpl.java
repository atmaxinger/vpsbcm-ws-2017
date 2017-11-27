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
        ServiceUtil.writeItem(flowerPlant,flowerPlantContainer,null,capi);
    }

    @Override
    public void putVegetablePlant(VegetablePlant vegetablePlant) {
        ServiceUtil.writeItem(vegetablePlant,vegetablePlantContainer,null,capi);
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
    public List<FlowerPlant> readAllFlowerPlants() {
        return ServiceUtil.readAllItems(flowerPlantContainer,null,capi);
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants() {
        return ServiceUtil.readAllItems(vegetablePlantContainer,null,capi);
    }

    @Override
    public List<Flower> readAllFlowers() {
        return ServiceUtil.readAllItems(flowerContainer,null,capi);
    }

    @Override
    public List<Vegetable> readAllVegetables() {
        return ServiceUtil.readAllItems(vegetableContainer,null,capi);
    }
}
