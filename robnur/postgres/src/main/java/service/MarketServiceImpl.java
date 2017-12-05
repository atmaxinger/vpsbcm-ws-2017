package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.MarketService;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import java.util.Arrays;
import java.util.List;

public class MarketServiceImpl extends MarketService {

    private static final String MARKET_BOUQUET_TABLE = "mbt";
    private static final String MARKET_VEGETABLE_BASKET_TABLE = "mvb";

    public MarketServiceImpl() {
        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                String table = ServiceUtil.getTableName(channelName, payload);

                switch (table) {
                    case MARKET_BOUQUET_TABLE:
                    case MARKET_VEGETABLE_BASKET_TABLE:
                        raiseChangedEvent();
                        break;
                }

            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(MARKET_BOUQUET_TABLE);
        PostgresHelper.setUpListen(MARKET_VEGETABLE_BASKET_TABLE);
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
        ServiceUtil.deleteItemById(bouquet.getId(),MARKET_BOUQUET_TABLE);
    }

    public List<VegetableBasket> readAllVegetableBaskets() {
        return ServiceUtil.readAllItems(MARKET_VEGETABLE_BASKET_TABLE,VegetableBasket.class);
    }

    public void sellVegetableBasket(VegetableBasket vegetableBasket) {
        ServiceUtil.deleteItemById(vegetableBasket.getId(),MARKET_VEGETABLE_BASKET_TABLE);
    }

    public static List<String> getTables() {
        return Arrays.asList(MARKET_BOUQUET_TABLE,MARKET_VEGETABLE_BASKET_TABLE);
    }
}
