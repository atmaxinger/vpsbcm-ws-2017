package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;

import java.net.URI;
import java.util.List;

public class MarketServiceImpl implements MarketService {

    public MarketServiceImpl(URI spaceUri) {
    }

    @Override
    public void putBouquet(Bouquet bouquet) {

    }

    @Override
    public int getAmountOfBouquets() {
        return 0;
    }

    @Override
    public void putVegetableBasket(VegetableBasket vegetableBasket) {

    }

    @Override
    public int getAmountOfVegetableBaskets() {
        return 0;
    }

    @Override
    public Bouquet getBouquet() {
        return null;
    }

    @Override
    public VegetableBasket getVegetableBasket() {
        return null;
    }

    @Override
    public List<Bouquet> readAllBouquets() {
        return null;
    }

    @Override
    public void sellBouquet(Bouquet bouquet) {

    }

    @Override
    public List<VegetableBasket> readAllVegetableBaskets() {
        return null;
    }

    @Override
    public void sellVegetableBasket(VegetableBasket vegetableBasket) {

    }
}
