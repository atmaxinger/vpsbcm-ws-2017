package at.ac.tuwien.complang.vpsbcm.robnur.shared;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Idable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Order<E extends Enum<E>,P> extends Idable {

    public enum OrderStatus {
        PLACED, PACKED, PAID
    }

    private OrderStatus orderStatus = OrderStatus.PLACED;
    private long timestamp;
    private String address;
    private HashMap<E, Integer> missingItems = new HashMap<>();
    private List<P> alreadyAcquiredItems = new LinkedList<>();

    public Order() {
        timestamp = (new Date()).getTime();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPlantAmount(E plantType, int amount) {
        missingItems.put(plantType, amount);
    }

    public HashMap<E, Integer> getMissingItems() {
        return missingItems;
    }

    public void setAlreadyAcquiredItem(P alreadyAcquiredItem, E type) {
        alreadyAcquiredItems.add(alreadyAcquiredItem);
        int oldValue = missingItems.get(type);
        missingItems.replace(type,oldValue-1);
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
