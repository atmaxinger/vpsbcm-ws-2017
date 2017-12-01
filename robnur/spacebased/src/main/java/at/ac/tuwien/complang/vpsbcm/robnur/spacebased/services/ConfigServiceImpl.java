package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.omg.CORBA.Any;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ConfigServiceImpl extends ConfigService {

    Capi capi;
    private NotificationManager notificationManager;

    ContainerReference flowerPlantCultivationInformationContainer;
    ContainerReference vegetablePlantCultivationInformationContainer;

    public ConfigServiceImpl(URI spaceUri) {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new AnyCoordinator(), new QueryCoordinator());

        try {
            flowerPlantCultivationInformationContainer = CapiUtil.lookupOrCreateContainer("flowerPlantCultivationInformationContainer", spaceUri, coordinators, null, capi);
            vegetablePlantCultivationInformationContainer = CapiUtil.lookupOrCreateContainer("vegetablePlantCultivationInformationContainer", spaceUri, coordinators, null, capi);

            notificationManager.createNotification(flowerPlantCultivationInformationContainer, (notification, operation, list) -> notifyFlowerCultivationInformationChanged(readAllFlowerPlantCultivationInformation(null)), Operation.WRITE);
            notificationManager.createNotification(vegetablePlantCultivationInformationContainer, (notification, operation, list) -> notifyVegetableCultivationInformationChanged(readAllVegetablePlantCultivationInformation(null)), Operation.WRITE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,flowerPlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,vegetablePlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public void deleteFlowerPlantCultivationInformation(String id, Transaction transaction) {
         ServiceUtil.getItemById(id,flowerPlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public void deleteVegetablePlantCultivationInformation(String id, Transaction transaction) {
        ServiceUtil.getItemById(id,vegetablePlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public void putFlowerPlantCultivationInformation(FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction) {
        ServiceUtil.writeItem(flowerPlantCultivationInformation,flowerPlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public void putVegetablePlantCultivationInformation(VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction) {
        ServiceUtil.writeItem(vegetablePlantCultivationInformation,vegetablePlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public List<FlowerPlantCultivationInformation> readAllFlowerPlantCultivationInformation(Transaction transaction) {
        return ServiceUtil.readAllItems(flowerPlantCultivationInformationContainer,transaction,capi);
    }

    @Override
    public List<VegetablePlantCultivationInformation> readAllVegetablePlantCultivationInformation(Transaction transaction) {
        return ServiceUtil.readAllItems(vegetablePlantCultivationInformationContainer,transaction,capi);
    }
}
