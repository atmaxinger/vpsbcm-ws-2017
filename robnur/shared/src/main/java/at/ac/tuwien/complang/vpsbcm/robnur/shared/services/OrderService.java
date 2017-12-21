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

    public abstract boolean canPlaceOrder(String address);

    public abstract boolean placeOrderForVegetableBasket(Order<VegetableType, Vegetable> order);

    public abstract boolean placeOrderForBouquet(Order<FlowerType, Flower> order);

    public abstract boolean putVegetableBasketOrder(Order<VegetableType, Vegetable> order, Transaction transaction);

    public abstract boolean putBouquetOrder(Order<FlowerType, Flower> order, Transaction transaction);

    public abstract boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address);

    public abstract boolean deliverBouquet(Bouquet bouquet, String address);

    public abstract Order<VegetableType,Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction);

    public abstract Order<FlowerType,Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction);

    public abstract List<Order<VegetableType,Vegetable>> readAllOrdersForVegetables(Transaction transaction);

    public abstract List<Order<FlowerType,Flower>> readAllOrdersForFlowers(Transaction transaction);

    public abstract void updateVegetableBasketOrderStatus(String id, Order.OrderStatus orderStatus);

    public abstract void updateBouquetOrderStatus(String id, Order.OrderStatus orderStatus);
}
