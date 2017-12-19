package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderServiceImpl extends OrderService {

    final static Logger logger = Logger.getLogger(OrderServiceImpl.class);

    private MzsCore core;
    private Capi capi;
    private ContainerReference flowerOrderContainer;
    private ContainerReference vegetableOrderContainer;
    private NotificationManager notificationManager;

    public OrderServiceImpl(URI serverUri) {
        core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        notificationManager = new NotificationManager(core);

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
    public boolean placeOrderForVegetableBasket(Order<VegetableType,Vegetable> order, Transaction transaction) {
        if(transaction == null){
            return ServiceUtil.writeItem(order,vegetableOrderContainer,capi);
        }
        return ServiceUtil.writeItem(order,vegetableOrderContainer,transaction,capi);
    }

    @Override
    public boolean placeOrderForBouquet(Order<FlowerType,Flower> order, Transaction transaction) {
        if(transaction == null){
            return ServiceUtil.writeItem(order,flowerOrderContainer,capi);
        }
        return ServiceUtil.writeItem(order,flowerOrderContainer,transaction,capi);
    }

    @Override
    public boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address) {
        URI customerSpaceUri = URI.create(address);
        try {
            ContainerReference deliveryVegetableBasketContainer = CapiUtil.lookupOrCreateContainer("deliveryVegetableBasketContainer", customerSpaceUri, Collections.singletonList(new AnyCoordinator()), null, capi);
            ServiceUtil.writeItem(vegetableBasket,deliveryVegetableBasketContainer,capi);
            return true;
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }
    }

    @Override
    public boolean deliverBouquet(Bouquet bouquet, String address) {
        URI customerSpaceUri = URI.create(address);
        try {
            ContainerReference deliveryBouquetContainer = CapiUtil.lookupOrCreateContainer("deliveryBouquetContainer", customerSpaceUri, Collections.singletonList(new AnyCoordinator()), null, capi);
            ServiceUtil.writeItem(bouquet,deliveryBouquetContainer,capi);
            return true;
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }
    }

    @Override
    public Order<VegetableType,Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction) {

        Property timestampProperty = Property.forName("timestamp");
        ComparableProperty growthProperty = ComparableProperty.forName("growth");

        Query query = new Query().sortup(timestampProperty).filter(growthProperty.equalTo(status));

        Selector selector = QueryCoordinator.newSelector(query, 1);

        List<Order<VegetableType,Vegetable>> result = ServiceUtil.getAllItems(vegetableOrderContainer,selector,transaction,capi);

        if (result == null || result.isEmpty()){
            return null;
        }

        return result.get(0);
    }

    @Override
    public Order<FlowerType,Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction) {

        Property timestampProperty = Property.forName("timestamp");
        ComparableProperty growthProperty = ComparableProperty.forName("growth");

        Query query = new Query().sortup(timestampProperty).filter(growthProperty.equalTo(status));

        Selector selector = QueryCoordinator.newSelector(query, 1);

        List<Order<FlowerType,Flower>> result = ServiceUtil.getAllItems(flowerOrderContainer,selector,transaction,capi);

        if (result == null || result.isEmpty()){
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<Order<VegetableType,Vegetable>> readAllOrdersForVegetables(Transaction transaction) {
        if(transaction == null){
            return ServiceUtil.readAllItems(vegetableOrderContainer,capi);
        }
        return ServiceUtil.readAllItems(vegetableOrderContainer,transaction,capi);
    }

    @Override
    public List<Order<FlowerType,Flower>> readAllOrdersForFlowers(Transaction transaction) {
        if(transaction == null){
            return ServiceUtil.readAllItems(flowerOrderContainer,capi);
        }
        return ServiceUtil.readAllItems(flowerOrderContainer,transaction,capi);
    }
}
