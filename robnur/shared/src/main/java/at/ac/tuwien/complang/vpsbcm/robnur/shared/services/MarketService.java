package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public abstract class MarketService {

    public interface Callback<T1,T2> {
        void handle(T1 p1, T2 p2);
    }

    protected Callback<List<VegetableBasket>, List<Bouquet>> marketChanged;
    public void onMarketChanged(Callback<List<VegetableBasket>, List<Bouquet>> marketChanged) {
        this.marketChanged = marketChanged;
    }

    public abstract void putBouquet(Bouquet bouquet);

    public abstract int getAmountOfBouquets();

    public abstract void putVegetableBasket(VegetableBasket vegetableBasket);

    public abstract int getAmountOfVegetableBaskets();

    public abstract List<Bouquet> readAllBouquets();

    public abstract void sellBouquet(Bouquet bouquet);

    public abstract List<VegetableBasket> readAllVegetableBaskets();

    public abstract void sellVegetableBasket(VegetableBasket vegetableBasket);
}
