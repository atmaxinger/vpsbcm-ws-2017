package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class CompostServiceImpl extends CompostService {

    Capi capi;
    private NotificationManager notificationManager;

    ContainerReference flowerPlantContainer;
    ContainerReference flowerContainer;
    ContainerReference vegetablePlantContainer;
    ContainerReference vegetableContainer;

    public CompostServiceImpl(URI spaceUri) {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        notificationManager = new NotificationManager(core);
        capi = new Capi(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator());

        try {
            flowerPlantContainer = CapiUtil.lookupOrCreateContainer("flowerPlantContainer",spaceUri,coordinators,null,capi);
            flowerContainer = CapiUtil.lookupOrCreateContainer("flowerContainer",spaceUri,coordinators,null,capi);
            vegetablePlantContainer = CapiUtil.lookupOrCreateContainer("vegetablePlantContainer",spaceUri,coordinators,null,capi);
            vegetableContainer = CapiUtil.lookupOrCreateContainer("vegetableContainer",spaceUri,coordinators,null,capi);

            notificationManager.createNotification(flowerPlantContainer, (notification, operation, list) -> notifyFlowerPlantsChanged(readAllFlowerPlants()), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(flowerContainer, (notification, operation, list) -> notifyFlowersChanged(readAllFlowers()), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(vegetablePlantContainer, (notification, operation, list) -> notifyVegetablePlantsChanged(readAllVegetablePlants()), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(vegetableContainer, (notification, operation, list) -> notifyVegetablesChanged(readAllVegetables()), Operation.WRITE, Operation.TAKE, Operation.DELETE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
