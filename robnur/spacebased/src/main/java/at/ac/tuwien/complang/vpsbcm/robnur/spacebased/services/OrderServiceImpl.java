package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class OrderServiceImpl extends OrderService {

    private MzsCore core;
    private Capi capi;
    private ContainerReference flowerOrderContainer;
    private ContainerReference vegetableOrderContainer;
    private NotificationManager notificationManager;

    public OrderServiceImpl(URI serverUri) {
        core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        try {
            flowerOrderContainer = CapiUtil.lookupOrCreateContainer("flowerOrderContainer", serverUri, Collections.singletonList(new QueryCoordinator()), null, capi);
            vegetableOrderContainer = CapiUtil.lookupOrCreateContainer("vegetableOrderContainer", serverUri, Collections.singletonList(new QueryCoordinator()), null, capi);

            notificationManager.createNotification(flowerOrderContainer, (notification, operation, list) -> notifyFlowerOrdersChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(vegetableOrderContainer, (notification, operation, list) -> notifiyVegetableOrdersChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean placeOrderForVegetables(Order<VegetableType> order) {
        return false;
    }

    @Override
    public boolean placeOrderForFlowers(Order<FlowerType> order) {
        return false;
    }

    @Override
    public boolean deliverVegetables(VegetableBasket basket, String address) {
        return false;
    }

    @Override
    public boolean deliverFlowers(Bouquet bouquet, String address) {
        return false;
    }

    @Override
    public Order<VegetableType> getOrderForVegetables(Order.OrderStatus status, Transaction transaction) {
        return null;
    }

    @Override
    public Order<FlowerType> getOrderForFlowers(Order.OrderStatus status, Transaction transaction) {
        return null;
    }

    @Override
    public List<Order<VegetableType>> readAllOrdersForVegetables(Transaction transaction) {
        return null;
    }

    @Override
    public List<Order<FlowerType>> readAllOrdersForFlowers(Transaction transaction) {
        return null;
    }
}
