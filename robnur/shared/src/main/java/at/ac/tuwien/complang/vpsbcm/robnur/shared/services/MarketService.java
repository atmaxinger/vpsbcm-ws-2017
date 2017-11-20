package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;

public interface MarketService {

    void putBouquet(Bouquet bouquet);

    int getAmountOfBouquets();

    void putVegetableBasket(VegetableBasket vegetableBasket);

    int getAmountOfVegetableBaskets();

    Bouquet getBouquet();

    VegetableBasket getVegetableBasket();

}
