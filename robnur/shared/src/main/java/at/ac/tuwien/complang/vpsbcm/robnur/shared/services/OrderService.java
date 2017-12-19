package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class OrderService {

    private StorageService.Callback<List<Order<VegetableType,Vegetable>>> vegetableOrdersChanged;
    private StorageService.Callback<List<Order<FlowerType,Flower>>> flowerOrdersChanged;

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

    public void onVegetableOrdersChanged(StorageService.Callback<List<Order<VegetableType,Vegetable>>> vegetableOrdersChanged) {
        this.vegetableOrdersChanged = vegetableOrdersChanged;
    }

    public void onFlowerOrdersChanged(StorageService.Callback<List<Order<FlowerType,Flower>>> flowerOrdersChanged) {
        this.flowerOrdersChanged = flowerOrdersChanged;
    }

    public abstract boolean placeOrderForVegetableBasket(Order<VegetableType,Vegetable> order, Transaction transaction);

    public abstract boolean placeOrderForBouquet(Order<FlowerType,Flower> order, Transaction transaction);

    public abstract boolean deliverVegetableBasket(VegetableBasket vegetableBasket, String address);

    public abstract boolean deliverBouquet(Bouquet bouquet, String address);

    public abstract Order<VegetableType,Vegetable> getNextVegetableBasketOrder(Order.OrderStatus status, Transaction transaction);

    public abstract Order<FlowerType,Flower> getNextBouquetOrder(Order.OrderStatus status, Transaction transaction);

    public abstract List<Order<VegetableType,Vegetable>> readAllOrdersForVegetables(Transaction transaction);

    public abstract List<Order<FlowerType,Flower>> readAllOrdersForFlowers(Transaction transaction);
}
