package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MarketServiceImpl extends MarketService {
    final static Logger logger = Logger.getLogger(MarketServiceImpl.class);

    private static final String MARKET_BOUQUET_TABLE = "mbt";
    private static final String MARKET_VEGETABLE_BASKET_TABLE = "mvb";

    private List<Listener> listeners = new LinkedList<>();

    private boolean exit = false;

    @Override
    public synchronized boolean isExit() {
        return exit;
    }

    @Override
    public synchronized void setExit(boolean exit) {
        this.exit = exit;
        if(exit == true) {
            for(Listener listener : listeners) {
                listener.shutdown();
            }
        }
    }

    public MarketServiceImpl() {
        try {
            Listener flowerListener = new Listener(MARKET_BOUQUET_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    raiseChangedEvent();
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(MARKET_VEGETABLE_BASKET_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    raiseChangedEvent();
                }
            };
            vegetableListener.start();

            listeners.add(flowerListener);
            listeners.add(vegetableListener);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    public void putBouquet(Bouquet bouquet, Transaction transaction) {
        if(transaction == null) {
            ServiceUtil.writeItem(bouquet,MARKET_BOUQUET_TABLE);
            return;
        }
        ServiceUtil.writeItem(bouquet,MARKET_BOUQUET_TABLE, transaction);
    }

    @Override
    public int getAmountOfBouquets() {
        return readAllBouquets().size();
    }

    @Override
    public void putVegetableBasket(VegetableBasket vegetableBasket, Transaction transaction) {
        if(transaction == null) {
            ServiceUtil.writeItem(vegetableBasket,MARKET_VEGETABLE_BASKET_TABLE);
            return;
        }
        ServiceUtil.writeItem(vegetableBasket,MARKET_VEGETABLE_BASKET_TABLE, transaction);
    }

    @Override
    public List<Bouquet> readAllBouquets() {
        return ServiceUtil.readAllItems(MARKET_BOUQUET_TABLE,Bouquet.class);
    }

    @Override
    public void sellBouquet(Bouquet bouquet) {
        try {
            ServiceUtil.deleteItemById(bouquet.getId(),MARKET_BOUQUET_TABLE);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    public List<VegetableBasket> readAllVegetableBaskets() {
        return ServiceUtil.readAllItems(MARKET_VEGETABLE_BASKET_TABLE,VegetableBasket.class);
    }

    @Override
    public void sellVegetableBasket(VegetableBasket vegetableBasket) {
        try {
            ServiceUtil.deleteItemById(vegetableBasket.getId(),MARKET_VEGETABLE_BASKET_TABLE);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public static List<String> getTables() {
        return Arrays.asList(MARKET_BOUQUET_TABLE,MARKET_VEGETABLE_BASKET_TABLE);
    }
}
