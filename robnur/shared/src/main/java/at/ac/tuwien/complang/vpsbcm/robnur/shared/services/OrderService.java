package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class OrderService {

    private StorageService.Callback<List<Order<VegetableType,Vegetable>>> vegetableOrdersChanged;
    private StorageService.Callback<List<Order<FlowerType,Flower>>> flowerOrdersChanged;
    private StorageService.Callback<List<Order<VegetableType,Vegetable>>> newVegetableOrdersChanged;
    private StorageService.Callback<List<Order<FlowerType,Flower>>> newFlowerOrdersChanged;
    private StorageService.Callback<Boolean> canPlaceOrderChanged;

    protected void notifiyVegetableOrdersChanged() {
        if(vegetableOrdersChanged != null) {
            vegetableOrdersChanged.handle(readAllOrdersForVegetables(null));
        }
    }

    protected void notifyFlowerOrdersChanged() {
        if(flowerOrdersChanged != null) {
            flowerOrdersChanged.handle(readAllOrdersForFlowers(null));
        }
    }

    protected void notifiyNewVegetableOrdersChanged() {
        if(newVegetableOrdersChanged != null) {
            newVegetableOrdersChanged.handle(readAllOrdersForVegetables(null));
        }
    }

    protected void notifyNewFlowerOrdersChanged() {
        if(newFlowerOrdersChanged != null) {
            newFlowerOrdersChanged.handle(readAllOrdersForFlowers(null));
        }
    }


    protected void notifyCanPlaceOrderChanged(boolean canPlaceOrder) {
        if(canPlaceOrderChanged != null) {
            canPlaceOrderChanged.handle(canPlaceOrder);
        }
    }

    public void onVegetableOrdersChanged(StorageService.Callback<List<Order<VegetableType,Vegetable>>> vegetableOrdersChanged) {
        this.vegetableOrdersChanged = vegetableOrdersChanged;
    }

    public void onFlowerOrdersChanged(StorageService.Callback<List<Order<FlowerType,Flower>>> flowerOrdersChanged) {
        this.flowerOrdersChanged = flowerOrdersChanged;
    }

    public void onNewVegetableOrdersChanged(StorageService.Callback<List<Order<VegetableType,Vegetable>>> newVegetableOrdersChanged) {
        this.newVegetableOrdersChanged = newVegetableOrdersChanged;
    }

    public void onNewFlowerOrdersChanged(StorageService.Callback<List<Order<FlowerType,Flower>>> newFlowerOrdersChanged) {
        this.newFlowerOrdersChanged = newFlowerOrdersChanged;
    }

    public void onCanPlaceOrderChanged(StorageService.Callback<Boolean> canPlaceOrderChanged) {
        this.canPlaceOrderChanged = canPlaceOrderChanged;
    }

    /**
     * checks if a order can be placed
     * @param address the address of the customer
     * @return true if a order can be placed, otherwise false
     */
    public abstract boolean canPlaceOrder(String address);

    /**
     * place a new order for a vegetable basket
     * @param order order to place
     * @return true if successful, otherwise false
     */
    public abstract boolean placeOrderForVegetableBasket(Order<VegetableType, Vegetable> order);

    /**
     * place a new order for a bouquet
     * @param order order to place
     * @return true if successful, otherwise false
     */
    public abstract boolean placeOrderForBouquet(Order<FlowerType, Flower> order);

    /**
     * place a order (that was already placed before) for a vegetable basket
     * @param order order to place
     * @return true if successful, otherwise false
     */
    public abstract boolean putVegetableBasketOrder(Order<VegetableType, Vegetable> order, Transaction transaction);

    /**
     * place a order (that was already placed before) for a bouquet
     * @param order order to place
     * @return true if successful, otherwise false
     */
    public abstract boolean putBouquetOrder(Order<FlowerType, Flower> order, Transaction transaction);

    /**
     * deliver a vegetable basket to a specific address
     * @param vegetableBasket the vegetable basket that should be delivered
     * @param address the destination address
     * @return true if successful, otherwise false
     */
    public abstract boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address);

    /**
     * deliver a bouquet to a specific address
     * @param bouquet the bouquet that should be delivered
     * @param address the destination address
     * @return true if successful, otherwise false
     */
    public abstract boolean deliverBouquet(Bouquet bouquet, String address);

    /**
     * get the next order for a vegetable basket from the queue
     * @param status the status of the order
     * @param transaction
     * @return the next order in the queue, null if there is no order, null if unsuccessful
     */
    public abstract Order<VegetableType,Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction);

    /**
     * get the next order for a bouquet from the queue
     * @param status the status of the order
     * @param transaction
     * @return the next order in the queue, null if there is no order, null if unsuccessful
     */
    public abstract Order<FlowerType,Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction);

    /**
     * reads all vegetable basket orders
     * @param transaction
     * @return a list of all orders, null if unsuccessful
     */
    public abstract List<Order<VegetableType,Vegetable>> readAllOrdersForVegetables(Transaction transaction);

    /**
     * reads all bouquet orders
     * @param transaction
     * @return a list of all orders, null if unsuccessful
     */
    public abstract List<Order<FlowerType,Flower>> readAllOrdersForFlowers(Transaction transaction);

    /**
     * update the status of a vegetable basket order
     * @param id the id of the order
     * @param orderStatus the new status
     */
    public abstract void updateVegetableBasketOrderStatus(String id, Order.OrderStatus orderStatus);

    /**
     * update the status of a bouquet order
     * @param id the id of the order
     * @param orderStatus the new status
     */
    public abstract void updateBouquetOrderStatus(String id, Order.OrderStatus orderStatus);
}
