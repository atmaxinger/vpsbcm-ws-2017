import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.impossibl.postgres.api.jdbc.PGConnection;
import service.*;
import sun.security.krb5.Config;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DbServer {
    public static void main(String args[]) {

        createTables(CompostServiceImpl.getTables());
        createNotifyFunction(CompostServiceImpl.getTables());

        createTables(ConfigServiceImpl.getTables());
        createNotifyFunction(ConfigServiceImpl.getTables());

        createTables(MarketServiceImpl.getTables());
        createNotifyFunction(MarketServiceImpl.getTables());

        createTables(PackingServiceImpl.getTables());
        createNotifyFunction(PackingServiceImpl.getTables());

        createTables(ResearchServiceImpl.getTables());
        createNotifyFunction(ResearchServiceImpl.getTables());

        createTables(GreenhouseServiceImpl.getTables());
        createNotifyFunction(GreenhouseServiceImpl.getTables());

        createTables(StorageServiceImpl.getTables());
        createNotifyFunction(StorageServiceImpl.getTables());

        StorageService storageService = new StorageServiceImpl();
        Water water = new Water();
        water.setAmount(250);
        storageService.putWater(water);

        createWaterTrigger("sw");

        ConfigService configService = new ConfigServiceImpl();
        putInitialFlowerPlantCultivationInformation(configService);
        putInitialVegetablePlantCultivationInformation(configService);
    }

    private static void createTables(List<String> tables){

        PGConnection connection = PostgresHelper.getConnection();

        for (String t:tables) {
            try {
                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE IF EXISTS " + t);
                statement.execute("CREATE TABLE " + t + "(ID BIGSERIAL PRIMARY KEY, DATA JSON NOT NULL)");
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createNotifyFunction(List<String> tables) {

        PGConnection connection = PostgresHelper.getConnection();

        for (String table : tables) {

            try {

                Statement statement = connection.createStatement();

                statement.execute(
                        String.format(
                                "CREATE OR REPLACE FUNCTION %s_function() RETURNS TRIGGER AS $$" +
                                        "        BEGIN" +
                                        "        PERFORM pg_notify('%s_notify', TG_OP);" +
                                        "        RETURN NULL;" +
                                        "        END; " +
                                        "$$ LANGUAGE plpgsql;"
                                , table, table)
                );

                statement.execute(
                        String.format(
                        "CREATE TRIGGER %s_trigger " +
                                "AFTER INSERT OR DELETE ON %s " +
                                "FOR EACH ROW EXECUTE PROCEDURE %s_function();"
                        , table, table, table));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createWaterTrigger(String waterTable) {
        PGConnection connection = PostgresHelper.getConnection();

        Statement statement = null;
        try {
            statement = connection.createStatement();

            statement.execute(
                    String.format("CREATE OR REPLACE FUNCTION put_back_water() RETURNS TRIGGER AS $$" +
                            " BEGIN " +
                            " PERFORM pg_sleep(1); " +
                            " INSERT INTO %s(data) VALUES('{\"amount\":250,\"id\":\"49c660b6-b9e9-4a47-9922-2bfeefaef67c\"}'); " +
                            " RETURN NULL; " +
                            " END; " +
                            " $$ LANGUAGE plpgsql;",
                            waterTable
                    )
            );

            statement.execute(
                    String.format(
                            "CREATE TRIGGER water_trigger " +
                                    "AFTER DELETE ON %s " +
                                    "EXECUTE PROCEDURE put_back_water();"
                            , waterTable));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void putInitialFlowerPlantCultivationInformation(ConfigService configService) {
        Transaction transaction = ((new TransactionServiceImpl())).beginTransaction(-1);

        if (configService.readAllFlowerPlantCultivationInformation(transaction).size() == 0) {

            FlowerPlantCultivationInformation flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.ROSE);
            flowerPlantCultivationInformation.setSoilAmount(20);
            flowerPlantCultivationInformation.setWaterAmount(250);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.TULIP);
            flowerPlantCultivationInformation.setSoilAmount(15);
            flowerPlantCultivationInformation.setWaterAmount(375);
            flowerPlantCultivationInformation.setFertilizerAmount(2);
            flowerPlantCultivationInformation.setGrowthRate(0.5f);
            flowerPlantCultivationInformation.setHarvest(2);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.DAISY);
            flowerPlantCultivationInformation.setSoilAmount(23);
            flowerPlantCultivationInformation.setWaterAmount(237);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.VIOLET);
            flowerPlantCultivationInformation.setSoilAmount(27);
            flowerPlantCultivationInformation.setWaterAmount(250);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);
        }

        transaction.commit();
    }

    public static void putInitialVegetablePlantCultivationInformation(ConfigService configService) {
        Transaction transaction = ((new TransactionServiceImpl())).beginTransaction(-1);

        if (configService.readAllVegetablePlantCultivationInformation(transaction).size() == 0) {

            VegetablePlantCultivationInformation vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.PEPPER);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(2);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation, transaction);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.TOMATO);
            vegetablePlantCultivationInformation.setSoilAmount(25);
            vegetablePlantCultivationInformation.setWaterAmount(600);
            vegetablePlantCultivationInformation.setFertilizerAmount(2);
            vegetablePlantCultivationInformation.setGrowthRate(0.35f);
            vegetablePlantCultivationInformation.setHarvest(3);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(3);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation, transaction);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.CARROT);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(2);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation, transaction);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.SALAD);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(1);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation, transaction);
        }

        transaction.commit();
    }
}
