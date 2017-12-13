package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;

import java.util.List;

public abstract class MarketService {

    public interface Callback<T1,T2> {
        void handle(T1 p1, T2 p2);
    }

    protected Callback<List<VegetableBasket>, List<Bouquet>> marketChanged;
    public void onMarketChanged(Callback<List<VegetableBasket>, List<Bouquet>> marketChanged) {
        this.marketChanged = marketChanged;
    }

    protected void raiseChangedEvent() {
        if(marketChanged != null) {
            marketChanged.handle(readAllVegetableBaskets(), readAllBouquets());
        }
    }

    /**
     * puts a bouquet in the market
     * @param bouquet
     * @param transaction
     */
    public abstract void putBouquet(Bouquet bouquet, Transaction transaction);

    /**
     * get the amount of bouquets that are in the market
     * @return number of bouquets in the market
     */
    public abstract int getAmountOfBouquets();

    /**
     * puts a vegetable basket in the market
     * @param vegetableBasket
     * @param transaction
     */
    public abstract void putVegetableBasket(VegetableBasket vegetableBasket, Transaction transaction);

    /**
     * get the amount of vegetable baskets that are in the market
     * @return number of vegetable baskets in the market
     */
    public abstract int getAmountOfVegetableBaskets();

    /**
     * reads all bouquets that are in the market
     * @return all bouquets that are in the market, empty list if no bouquets are in the market, null if unsuccessful
     */
    public abstract List<Bouquet> readAllBouquets();

    /**
     * remove a bouquet form the market
     * @param bouquet to sell
     */
    public abstract void sellBouquet(Bouquet bouquet);

    /**
     * reads all vegetable baskets that are in the market
     * @return all vegetable baskets that are in the market, empty list if no vegetable baskets are in the market, null if unsuccessful
     */
    public abstract List<VegetableBasket> readAllVegetableBaskets();

    /**
     * remove a vegetable basket form the market
     * @param vegetableBasket to sell
     */
    public abstract void sellVegetableBasket(VegetableBasket vegetableBasket);

    /**
     * checks if notifications are stopped
     * @return true if notifications are stopped else false
     */
    public abstract boolean isExit();
}
