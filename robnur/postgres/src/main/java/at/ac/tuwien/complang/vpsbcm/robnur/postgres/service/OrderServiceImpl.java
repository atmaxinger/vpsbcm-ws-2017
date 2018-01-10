package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class OrderServiceImpl extends OrderService {

    final static Logger logger = Logger.getLogger(MarketServiceImpl.class);

    private static final String NEW_FLOWER_ORDER_TABLE = "nfot";
    private static final String NEW_VEGETABLE_ORDER_TABLE = "nvot";
    private static final String FLOWER_ORDER_TABLE = "fot";
    private static final String VEGETABLE_ORDER_TABLE = "vot";


    public OrderServiceImpl() {

        try {
            Listener flowerListener = null;
            flowerListener = new Listener(FLOWER_ORDER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowerOrdersChanged();
                    notifyCanPlaceOrderChanged(false);
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(VEGETABLE_ORDER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifiyVegetableOrdersChanged();
                    notifyCanPlaceOrderChanged(false);
                }
            };
            vegetableListener.start();


            Listener newFlowerListener = null;
            newFlowerListener = new Listener(NEW_FLOWER_ORDER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyNewFlowerOrdersChanged();
                    notifyCanPlaceOrderChanged(false);
                }
            };
            newFlowerListener.start();

            Listener newVegetableListener = new Listener(NEW_VEGETABLE_ORDER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifiyNewVegetableOrdersChanged();
                    notifyCanPlaceOrderChanged(false);
                }
            };
            newVegetableListener.start();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canPlaceOrder(String address) {
        List<Order<FlowerType, Flower>> allFlowerOrders = readAllOrdersForFlowers(null);
        List<Order<VegetableType, Vegetable>> allVegetableOrders = readAllOrdersForVegetables(null);

        for(Order o : allFlowerOrders) {
            if(o.getOrderStatus() == Order.OrderStatus.PLACED || o.getOrderStatus() == Order.OrderStatus.PACKED) {
                if(o.getAddress().equals(address)) {
                    return false;
                }
            }
        }

        for(Order o : allVegetableOrders) {
            if(o.getOrderStatus() == Order.OrderStatus.PLACED || o.getOrderStatus() == Order.OrderStatus.PACKED) {
                if(o.getAddress().equals(address)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean placeOrderForVegetableBasket(Order<VegetableType, Vegetable> order) {
        return ServiceUtil.writeItem(order,NEW_VEGETABLE_ORDER_TABLE);
    }

    @Override
    public boolean placeOrderForBouquet(Order<FlowerType, Flower> order) {
        return ServiceUtil.writeItem(order,NEW_FLOWER_ORDER_TABLE);
    }

    @Override
    public boolean putVegetableBasketOrder(Order<VegetableType, Vegetable> order, Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.writeItem(order, VEGETABLE_ORDER_TABLE);
        }
        return ServiceUtil.writeItem(order,VEGETABLE_ORDER_TABLE,transaction);
    }

    @Override
    public boolean putBouquetOrder(Order<FlowerType, Flower> order, Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.writeItem(order, FLOWER_ORDER_TABLE);
        }
        return ServiceUtil.writeItem(order,FLOWER_ORDER_TABLE,transaction);
    }

    @Override
    public boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address) {
        return ServiceUtil.writeItemIntoForeignDb(vegetableBasket, address, DeliveryStorageServiceImpl.VEGETABLE_BASKET_DELIVERY_TABLE);
    }

    @Override
    public boolean deliverBouquet(Bouquet bouquet, String address) {
        return ServiceUtil.writeItemIntoForeignDb(bouquet, address, DeliveryStorageServiceImpl.BOUQUET_DELIVERY_TABLE);
    }

    @Override
    public Order<VegetableType, Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction) {
        logger.debug("getNextVegetableBasketOrder()");

        TypeReference<Order<VegetableType, Vegetable>> typeReference = new TypeReference<Order<VegetableType, Vegetable>>() {};
        Order<VegetableType, Vegetable> result = ServiceUtil.getItemByParameter("'orderStatus'",status.name(),VEGETABLE_ORDER_TABLE, typeReference,transaction);

        if(result == null){
            result = ServiceUtil.getItemByParameter("'orderStatus'",status.name(),NEW_VEGETABLE_ORDER_TABLE, typeReference,transaction);
        }

        return result;
    }

    @Override
    public Order<FlowerType, Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction) {
        TypeReference<Order<FlowerType, Flower>> typeReference = new TypeReference<Order<FlowerType, Flower>>() {};

        Order<FlowerType, Flower> result =  ServiceUtil.getItemByParameter("'orderStatus'",status.name(),FLOWER_ORDER_TABLE, typeReference,transaction);

        if (result == null){
            result = ServiceUtil.getItemByParameter("'orderStatus'",status.name(),NEW_FLOWER_ORDER_TABLE, typeReference,transaction);
        }

        return result;
    }

    @Override
    public List readAllOrdersForVegetables(Transaction transaction) {
        List result;
        TypeReference<Order<VegetableType, Vegetable>> typeReference = new TypeReference<Order<VegetableType, Vegetable>>() {};

        if(transaction == null) {
            result = ServiceUtil.readAllItems(VEGETABLE_ORDER_TABLE,typeReference);
            result.addAll(ServiceUtil.readAllItems(NEW_VEGETABLE_ORDER_TABLE,typeReference));

            return result;
        }

        result = ServiceUtil.readAllItems(VEGETABLE_ORDER_TABLE,typeReference,transaction);
        result.addAll(ServiceUtil.readAllItems(NEW_VEGETABLE_ORDER_TABLE,typeReference,transaction));

        return result;
    }

    @Override
    public List readAllOrdersForFlowers(Transaction transaction) {
        List result;
        TypeReference<Order<FlowerType, Flower>> typeReference = new TypeReference<Order<FlowerType, Flower>>() {};

        if(transaction == null) {
            result = ServiceUtil.readAllItems(FLOWER_ORDER_TABLE, typeReference);
            result.addAll(ServiceUtil.readAllItems(NEW_FLOWER_ORDER_TABLE, typeReference));

            return result;
        }
        result = ServiceUtil.readAllItems(FLOWER_ORDER_TABLE, typeReference,transaction);
        result.addAll(ServiceUtil.readAllItems(NEW_FLOWER_ORDER_TABLE, typeReference,transaction));

        return result;
    }

    @Override
    public void updateVegetableBasketOrderStatus(String id, Order.OrderStatus orderStatus) {
        TypeReference<Order<VegetableType, Vegetable>> typeReference = new TypeReference<Order<VegetableType, Vegetable>>() {};

        TransactionService transactionService = new TransactionServiceImpl();
        Transaction transaction = transactionService.beginTransaction(-1);

        Order order = ServiceUtil.getItemById(id,VEGETABLE_ORDER_TABLE,typeReference,transaction);
        transaction.commit();

        order.setOrderStatus(orderStatus);
        ServiceUtil.writeItem(order,VEGETABLE_ORDER_TABLE);

    }

    @Override
    public void updateBouquetOrderStatus(String id, Order.OrderStatus orderStatus) {
        TypeReference<Order<FlowerType, Flower>> typeReference = new TypeReference<Order<FlowerType, Flower>>() {};

        TransactionService transactionService = new TransactionServiceImpl();
        Transaction transaction = transactionService.beginTransaction(-1);
        Order order = ServiceUtil.getItemById(id,FLOWER_ORDER_TABLE, typeReference,transaction);
        transaction.commit();
        order.setOrderStatus(orderStatus);
        ServiceUtil.writeItem(order,FLOWER_ORDER_TABLE);
    }

    public static List<String> getTables() {
        return Arrays.asList(NEW_FLOWER_ORDER_TABLE,NEW_VEGETABLE_ORDER_TABLE,FLOWER_ORDER_TABLE,VEGETABLE_ORDER_TABLE);
    }
}
