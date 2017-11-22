package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;

import java.util.List;

public interface MarketService {

    void putBouquet(Bouquet bouquet);

    int getAmountOfBouquets();

    void putVegetableBasket(VegetableBasket vegetableBasket);

    int getAmountOfVegetableBaskets();

    Bouquet getBouquet();

    VegetableBasket getVegetableBasket();

    List<Bouquet> readAllBouquets();

    void sellBouquet(Bouquet bouquet);

    List<VegetableBasket> readAllVegetableBaskets();

    void sellVegetableBasket(VegetableBasket vegetableBasket);
}
