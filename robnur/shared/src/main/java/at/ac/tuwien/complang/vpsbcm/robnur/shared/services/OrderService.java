package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class OrderService {

    private StorageService.Callback<List<Order<VegetableType>>> vegetableOrdersChanged;
    private StorageService.Callback<List<Order<FlowerType>>> flowerOrdersChanged;

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

    public void onVegetableOrdersChanged(StorageService.Callback<List<Order<VegetableType>>> vegetableOrdersChanged) {
        this.vegetableOrdersChanged = vegetableOrdersChanged;
    }

    public void onFlowerOrdersChanged(StorageService.Callback<List<Order<FlowerType>>> flowerOrdersChanged) {
        this.flowerOrdersChanged = flowerOrdersChanged;
    }

    public abstract boolean placeOrderForVegetables(Order<VegetableType> order);
    public abstract boolean placeOrderForFlowers(Order<FlowerType> order);

    public abstract boolean deliverVegetables(VegetableBasket basket, String address);
    public abstract boolean deliverFlowers(Bouquet bouquet, String address);

    public abstract Order<VegetableType> getOrderForVegetables(Order.OrderStatus status, Transaction transaction);
    public abstract Order<FlowerType> getOrderForFlowers(Order.OrderStatus status, Transaction transaction);

    public abstract List<Order<VegetableType>> readAllOrdersForVegetables(Transaction transaction);
    public abstract List<Order<FlowerType>> readAllOrdersForFlowers(Transaction transaction);
}
