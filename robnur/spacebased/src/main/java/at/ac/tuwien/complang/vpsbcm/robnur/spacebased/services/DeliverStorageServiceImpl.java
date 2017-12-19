package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class DeliverStorageServiceImpl extends DeliveryStorageService {

    private Capi capi;
    private ContainerReference vegetableBasketsContainer;
    private ContainerReference bouqetsContainer;

    public DeliverStorageServiceImpl(URI spaceUri) {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
        NotificationManager notificationManager = new NotificationManager(core);

        try {
            vegetableBasketsContainer = CapiUtil.lookupOrCreateContainer("deliveryVegetableBasketContainer", spaceUri, Collections.singletonList(new AnyCoordinator()), null, capi);
            bouqetsContainer = CapiUtil.lookupOrCreateContainer("deliveryBouquetContainer", spaceUri, Collections.singletonList(new AnyCoordinator()), null, capi);

            notificationManager.createNotification(bouqetsContainer, (notification, operation, list) -> notifyBouqetsChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(vegetableBasketsContainer, (notification, operation, list) -> notifyVegetableBasketsChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<VegetableBasket> readAllVegetableBaskets() {
        return ServiceUtil.readAllItems(vegetableBasketsContainer, null, capi);
    }

    @Override
    public List<Bouquet> readAllBouqets() {
        return ServiceUtil.readAllItems(bouqetsContainer, null, capi);
    }
}
