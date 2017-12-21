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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderServiceImpl extends OrderService {

    final static Logger logger = Logger.getLogger(OrderServiceImpl.class);

    private MzsCore core;
    private Capi capi;
    private ContainerReference newFlowerOrderContainer;
    private ContainerReference newVegetableOrderContainer;
    private ContainerReference flowerOrderContainer;
    private ContainerReference vegetableOrderContainer;
    private ContainerReference addressContainer;
    private NotificationManager notificationManager;

    public OrderServiceImpl(URI serverUri) {
        core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        notificationManager = new NotificationManager(core);

        List<Coordinator> coordinators = Arrays.asList(new QueryCoordinator(), new AnyCoordinator());

        try {
            newFlowerOrderContainer = CapiUtil.lookupOrCreateContainer("newFlowerOrderContainer", serverUri, coordinators, null, capi);
            newVegetableOrderContainer = CapiUtil.lookupOrCreateContainer("newVegetableOrderContainer", serverUri, coordinators, null, capi);
            flowerOrderContainer = CapiUtil.lookupOrCreateContainer("flowerOrderContainer", serverUri, coordinators, null, capi);
            vegetableOrderContainer = CapiUtil.lookupOrCreateContainer("vegetableOrderContainer", serverUri, coordinators, null, capi);
            addressContainer = CapiUtil.lookupOrCreateContainer("addressContainer", serverUri, Arrays.asList(new LabelCoordinator(),new AnyCoordinator()), null, capi);

            notificationManager.createNotification(flowerOrderContainer, (notification, operation, list) -> notifyFlowerOrdersChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(vegetableOrderContainer, (notification, operation, list) -> notifiyVegetableOrdersChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);

            notificationManager.createNotification(newFlowerOrderContainer, (notification, operation, list) -> notifyNewFlowerOrdersChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);
            notificationManager.createNotification(newVegetableOrderContainer, (notification, operation, list) -> notifiyNewVegetableOrdersChanged(), Operation.WRITE, Operation.TAKE, Operation.DELETE);


            notificationManager.createNotification(addressContainer, (notification, operation, list) -> notifyCanPlaceOrderChanged(false), Operation.WRITE, Operation.TAKE, Operation.DELETE);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canPlaceOrder(String address) {
        /*ComparableProperty addressProperty = ComparableProperty.forName("address");
        ComparableProperty orderStatusProperty = ComparableProperty.forName("orderStatus");

        Matchmaker matchmaker = Matchmakers.and(addressProperty.equalTo(addressProperty),Matchmakers.or(orderStatusProperty.equalTo(Order.OrderStatus.PLACED), orderStatusProperty.equalTo(Order.OrderStatus.PACKED)));
        Query query = new Query().filter(addressProperty.equalTo(address)).filter(matchmaker);
        Selector selector = QueryCoordinator.newSelector(query, 1);

        List<Order<FlowerType,Flower>> result = ServiceUtil.readAllItems(flowerOrderContainer,selector,null,capi);
        if (result == null || result.isEmpty()){
            logger.debug("canPlaceOrder result is " + result);
            result = ServiceUtil.readAllItems(vegetableOrderContainer,selector,null,capi);
            if (result == null || result.isEmpty()){
                logger.debug("canPlaceOrder result is " + result);
                return true;
            }
        }

        logger.debug("--- canPlaceOrder result is " + result);

        return false;*/

       List<String> allAddresses = ServiceUtil.readAllItems(addressContainer,null,capi);
       if(allAddresses != null){
           for(String a : allAddresses) {
               if(a.equals(address)) {
                   return false;
               }
           }
       }

       return true;
    }

    @Override
    public boolean placeOrderForVegetableBasket(Order<VegetableType, Vegetable> order) {
        ServiceUtil.writeItem(new Entry(order.getAddress(), LabelCoordinator.newCoordinationData(order.getAddress())), addressContainer, null, capi);

        return ServiceUtil.writeItem(order,newVegetableOrderContainer,null,capi);
    }

    @Override
    public boolean placeOrderForBouquet(Order<FlowerType, Flower> order) {
        ServiceUtil.writeItem(new Entry(order.getAddress(), LabelCoordinator.newCoordinationData(order.getAddress())), addressContainer, null, capi);

        return ServiceUtil.writeItem(order,flowerOrderContainer,null,capi);
    }

    @Override
    public boolean putVegetableBasketOrder(Order<VegetableType, Vegetable> order, Transaction transaction) {
        return ServiceUtil.writeItem(order,vegetableOrderContainer,transaction,capi);
    }

    @Override
    public boolean putBouquetOrder(Order<FlowerType, Flower> order, Transaction transaction) {
        return ServiceUtil.writeItem(order,flowerOrderContainer,transaction,capi);
    }

    @Override
    public boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address) {
        URI customerSpaceUri = URI.create(address);
        try {
            ServiceUtil.getItem(LabelCoordinator.newSelector(address), addressContainer, null, capi);
            ContainerReference deliveryVegetableBasketContainer = CapiUtil.lookupOrCreateContainer("deliveryVegetableBasketContainer", customerSpaceUri, Collections.singletonList(new AnyCoordinator()), null, capi);
            ServiceUtil.writeItem(vegetableBasket,deliveryVegetableBasketContainer,null, capi);
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
            ServiceUtil.getItem(LabelCoordinator.newSelector(address), addressContainer, null, capi);
            ContainerReference deliveryBouquetContainer = CapiUtil.lookupOrCreateContainer("deliveryBouquetContainer", customerSpaceUri, Collections.singletonList(new AnyCoordinator()), null, capi);
            ServiceUtil.writeItem(bouquet,deliveryBouquetContainer,null,capi);
            return true;
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }
    }

    @Override
    public Order<VegetableType,Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction) {

        Property timestampProperty = Property.forName("timestamp");
        ComparableProperty growthProperty = ComparableProperty.forName("orderStatus");

        Query query = new Query().sortup(timestampProperty).filter(growthProperty.equalTo(status));

        Selector selector = QueryCoordinator.newSelector(query, 1);

        List<Order<VegetableType,Vegetable>> result = ServiceUtil.getAllItems(vegetableOrderContainer,selector,transaction,capi);

        if (result == null || result.isEmpty()){
            result = ServiceUtil.getAllItems(newVegetableOrderContainer,selector,transaction,capi);
            if (result == null || result.isEmpty()) {
                return null;
            }
        }

        return result.get(0);
    }

    @Override
    public Order<FlowerType,Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction) {

        Property timestampProperty = Property.forName("timestamp");
        ComparableProperty growthProperty = ComparableProperty.forName("orderStatus");

        Query query = new Query().sortup(timestampProperty).filter(growthProperty.equalTo(status));

        Selector selector = QueryCoordinator.newSelector(query, 1);

        List<Order<FlowerType,Flower>> result = ServiceUtil.getAllItems(flowerOrderContainer,selector,transaction,capi);

        if (result == null || result.isEmpty()){
            result = ServiceUtil.getAllItems(flowerOrderContainer,selector,transaction,capi);
            if (result == null || result.isEmpty()) {
                return null;
            }
        }

        return result.get(0);
    }

    @Override
    public List<Order<VegetableType,Vegetable>> readAllOrdersForVegetables(Transaction transaction) {
        List result;
        result = ServiceUtil.readAllItems(newVegetableOrderContainer,transaction,capi);
        result.addAll(ServiceUtil.readAllItems(vegetableOrderContainer,transaction,capi));
        return result;
    }

    @Override
    public List<Order<FlowerType,Flower>> readAllOrdersForFlowers(Transaction transaction) {
        List result;
        result = ServiceUtil.readAllItems(newFlowerOrderContainer,transaction,capi);
        result.addAll(ServiceUtil.readAllItems(flowerOrderContainer,transaction,capi));
        return result;
    }

    @Override
    public void updateVegetableBasketOrderStatus(String id, Order.OrderStatus orderStatus) {
        Order order = ServiceUtil.getItemById(id,vegetableOrderContainer,null,capi);
        order.setOrderStatus(orderStatus);
        ServiceUtil.writeItem(order,vegetableOrderContainer,null,capi);
    }

    @Override
    public void updateBouquetOrderStatus(String id, Order.OrderStatus orderStatus) {
        Order order = ServiceUtil.getItemById(id,flowerOrderContainer,null,capi);
        order.setOrderStatus(orderStatus);
        ServiceUtil.writeItem(order,flowerOrderContainer,null,capi);
    }
}
