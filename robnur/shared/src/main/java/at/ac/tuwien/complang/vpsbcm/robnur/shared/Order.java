package at.ac.tuwien.complang.vpsbcm.robnur.shared;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Idable;

import java.util.Date;
import java.util.HashMap;

public class Order<E extends Enum<E>> extends Idable {

    public enum OrderStatus {
        PLACED, PACKED, PAID
    }

    private OrderStatus orderStatus = OrderStatus.PLACED;
    private long timestamp;
    private String address;
    private HashMap<E, Integer> orders = new HashMap<>();


    public Order() {
        timestamp = (new Date()).getTime();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPlantAmount(E plantType, int amount) {
        orders.put(plantType, amount);
    }

    public HashMap<E, Integer> getOrders() {
        return orders;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
