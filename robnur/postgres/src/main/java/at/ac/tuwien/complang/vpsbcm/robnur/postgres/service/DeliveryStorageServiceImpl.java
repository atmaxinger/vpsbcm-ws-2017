package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DeliveryStorageServiceImpl extends DeliveryStorageService {
    final static Logger logger = Logger.getLogger(MarketServiceImpl.class);

    public static final String BOUQUET_DELIVERY_TABLE = "bd";
    public static final String VEGETABLE_BASKET_DELIVERY_TABLE = "vbd";

    private String connectionString;

    public DeliveryStorageServiceImpl(String connectionString) {
        this.connectionString = connectionString;


        try {
            Listener flowerListener = null;
            flowerListener = new Listener(BOUQUET_DELIVERY_TABLE, connectionString) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyBouqetsChanged();
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(VEGETABLE_BASKET_DELIVERY_TABLE, connectionString) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetableBasketsChanged();
                }
            };
            vegetableListener.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<VegetableBasket> readAllVegetableBaskets() {
        Connection connection = PostgresHelper.getConnectionForUrl(connectionString);
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TransactionImpl transaction = new TransactionImpl(connection, "READ ALL DELIVERED VEGETABLE BASKETS");

        List<VegetableBasket> result = ServiceUtil.readAllItems(VEGETABLE_BASKET_DELIVERY_TABLE,VegetableBasket.class,transaction);
        transaction.commit();
        return result;
    }

    @Override
    public List<Bouquet> readAllBouquets() {
        Connection connection = PostgresHelper.getConnectionForUrl(connectionString);
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TransactionImpl transaction = new TransactionImpl(connection, "READ ALL DELIVERED BOUQETS");

        List<Bouquet> result = ServiceUtil.readAllItems(BOUQUET_DELIVERY_TABLE,Bouquet.class,transaction);
        transaction.commit();
        return result;
    }

    public static List<String> getTables() {
        return Arrays.asList(BOUQUET_DELIVERY_TABLE,VEGETABLE_BASKET_DELIVERY_TABLE);
    }
}
