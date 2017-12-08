package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MarketServiceImpl extends MarketService {

    private static final String MARKET_BOUQUET_TABLE = "mbt";
    private static final String MARKET_VEGETABLE_BASKET_TABLE = "mvb";

    public MarketServiceImpl() {
        try {
            Listener flowerListener = new Listener(MARKET_BOUQUET_TABLE) {
                @Override
                public void onNotify() {
                    raiseChangedEvent();
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(MARKET_VEGETABLE_BASKET_TABLE) {
                @Override
                public void onNotify() {
                    raiseChangedEvent();
                }
            };
            vegetableListener.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void putBouquet(Bouquet bouquet) {
        ServiceUtil.writeItem(bouquet,MARKET_BOUQUET_TABLE);
    }

    public int getAmountOfBouquets() {
        return readAllBouquets().size();
    }

    public void putVegetableBasket(VegetableBasket vegetableBasket) {
        ServiceUtil.writeItem(vegetableBasket,MARKET_VEGETABLE_BASKET_TABLE);
    }

    public int getAmountOfVegetableBaskets() {
        return readAllVegetableBaskets().size();
    }

    public List<Bouquet> readAllBouquets() {
        return ServiceUtil.readAllItems(MARKET_BOUQUET_TABLE,Bouquet.class);
    }

    public void sellBouquet(Bouquet bouquet) {
        try {
            ServiceUtil.deleteItemById(bouquet.getId(),MARKET_BOUQUET_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<VegetableBasket> readAllVegetableBaskets() {
        return ServiceUtil.readAllItems(MARKET_VEGETABLE_BASKET_TABLE,VegetableBasket.class);
    }

    public void sellVegetableBasket(VegetableBasket vegetableBasket) {
        try {
            ServiceUtil.deleteItemById(vegetableBasket.getId(),MARKET_VEGETABLE_BASKET_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTables() {
        return Arrays.asList(MARKET_BOUQUET_TABLE,MARKET_VEGETABLE_BASKET_TABLE);
    }
}
