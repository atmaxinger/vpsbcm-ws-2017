package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class OrderServiceImpl extends OrderService {

    final static Logger logger = Logger.getLogger(MarketServiceImpl.class);

    private static final String FLOWER_ORDER_TABLE = "fot";
    private static final String VEGETABLE_ORDER_TABLE = "vot";

    public OrderServiceImpl() {
    }

    @Override
    public boolean canPlaceOrder(String address) {
        if(ServiceUtil.readAllItems(FLOWER_ORDER_TABLE,Order.class).size() >= 1){
            return false;
        } else if(ServiceUtil.readAllItems(VEGETABLE_ORDER_TABLE,Order.class).size() >= 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean placeOrderForVegetableBasket(Order<VegetableType, Vegetable> order, Transaction transaction) {
        return ServiceUtil.writeItem(order,VEGETABLE_ORDER_TABLE,transaction);
    }

    @Override
    public boolean placeOrderForBouquet(Order<FlowerType, Flower> order, Transaction transaction) {
        return ServiceUtil.writeItem(order,FLOWER_ORDER_TABLE,transaction);
    }

    @Override
    public boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address) {
        return ServiceUtil.writeItem(vegetableBasket,address);
    }

    @Override
    public boolean deliverBouquet(Bouquet bouquet, String address) {
        return ServiceUtil.writeItem(bouquet,address);
    }

    @Override
    public Order<VegetableType, Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'orderStatus'",status.name(),VEGETABLE_ORDER_TABLE,Order.class,transaction);
    }

    @Override
    public Order<FlowerType, Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'orderStatus'",status.name(),FLOWER_ORDER_TABLE,Order.class,transaction);
    }

    @Override
    public List readAllOrdersForVegetables(Transaction transaction) {
        return ServiceUtil.readAllItems(VEGETABLE_ORDER_TABLE,Order.class,transaction);
    }

    @Override
    public List readAllOrdersForFlowers(Transaction transaction) {
        return ServiceUtil.readAllItems(FLOWER_ORDER_TABLE,Order.class,transaction);
    }

    @Override
    public void updateVegetableBasketOrderStatus(String id, Order.OrderStatus orderStatus) {
        TransactionService transactionService = new TransactionServiceImpl();
        Order order = ServiceUtil.getItemById(id,VEGETABLE_ORDER_TABLE,Order.class,transactionService.beginTransaction(-1));
        order.setOrderStatus(orderStatus);
        ServiceUtil.writeItem(order,VEGETABLE_ORDER_TABLE);
    }

    @Override
    public void updateBouquetOrderStatus(String id, Order.OrderStatus orderStatus) {
        TransactionService transactionService = new TransactionServiceImpl();
        Order order = ServiceUtil.getItemById(id,FLOWER_ORDER_TABLE,Order.class,transactionService.beginTransaction(-1));
        order.setOrderStatus(orderStatus);
        ServiceUtil.writeItem(order,FLOWER_ORDER_TABLE);
    }

    public static List<String> getTables() {
        return Arrays.asList(FLOWER_ORDER_TABLE,VEGETABLE_ORDER_TABLE);
    }
}
