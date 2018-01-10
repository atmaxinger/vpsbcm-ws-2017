package at.ac.tuwien.complang.vpsbcm.robnur.postgres;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.service.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class InitDb {
    final static Logger logger = Logger.getLogger(InitDb.class);

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

        createTables(OrderServiceImpl.getTables());
        createNotifyFunction(OrderServiceImpl.getTables());
        
        createWaterTrigger("sw");

        insertInitialWaterToken();

        createGreenhouseTrigger();

        ConfigService configService = new ConfigServiceImpl();
        putInitialFlowerPlantCultivationInformation(configService);
        putInitialVegetablePlantCultivationInformation(configService);

        System.out.println("FINISHED - you can quit me now");
    }

    private static void createTables(List<String> tables){

        Connection connection = PostgresHelper.getNewConnection("create table",-1);

        for (String t:tables) {
            try {
                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE IF EXISTS " + t);
                statement.execute("CREATE TABLE " + t + "(ID BIGSERIAL PRIMARY KEY, DATA JSON NOT NULL)");
                statement.close();
            } catch (SQLException e) {
                logger.trace("EXCEPTION", e);
            }
        }

        try {
            connection.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    private static void createNotifyFunction(List<String> tables) {

        Connection connection = PostgresHelper.getNewConnection("create notify",-1);

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
                                "AFTER INSERT OR DELETE OR UPDATE ON %s " +
                                "FOR EACH ROW EXECUTE PROCEDURE %s_function();"
                        , table, table, table));

                statement.close();
            } catch (SQLException e) {
                logger.trace("EXCEPTION", e);
            }
        }

        try {
            connection.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

    }

    private static void createWaterTrigger(String waterTable) {
        Connection connection = PostgresHelper.getNewConnection("asdfcreate water trigger",-1);

        try {
            Statement statement = connection.createStatement();

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

            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    private static void createGreenhouseTrigger(){
        logger.debug("creating greenhouse trigger...");

        Connection connection = PostgresHelper.getNewConnection("greenhouseTrigger",-1);
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE OR REPLACE FUNCTION check_greenhouse_count() RETURNS trigger AS $$ " +
                    "    DECLARE " +
                    "        flower_count int; " +
                    "        vegetable_count int; " +
                    "    BEGIN " +
                    "         SELECT " +
                    "           count(*) into flower_count " +
                    "           FROM gfp; " +
                    "         SELECT " +
                    "           count(*) into vegetable_count " +
                    "           FROM gvp; " +
                    "         IF ((flower_count + vegetable_count) >= 20) THEN " +
                    "           RAISE EXCEPTION 'too much plants in greenhouse'; " +
                    "         END IF; " +
                    "        RETURN NEW;" +
                    "     END; " +
                    "$$ LANGUAGE plpgsql; " +
                    "CREATE TRIGGER gvp_check_count BEFORE INSERT ON gvp " +
                    "    FOR EACH ROW EXECUTE PROCEDURE check_greenhouse_count(); " +
                    "CREATE TRIGGER gfp_check_count BEFORE INSERT ON gfp " +
                    "    FOR EACH ROW EXECUTE PROCEDURE check_greenhouse_count();");

            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }


    public static void putInitialFlowerPlantCultivationInformation(ConfigService configService) {

        if (configService.readAllFlowerPlantCultivationInformation(null).size() == 0) {

            TransactionService transactionService = new TransactionServiceImpl();
            Transaction transaction = transactionService.beginTransaction(-1);

            FlowerPlantCultivationInformation flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.ROSE);
            flowerPlantCultivationInformation.setSoilAmount(20);
            flowerPlantCultivationInformation.setWaterAmount(250);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);
            flowerPlantCultivationInformation.setVulnerability(25);
            flowerPlantCultivationInformation.setPrice(50);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation,transaction);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.TULIP);
            flowerPlantCultivationInformation.setSoilAmount(15);
            flowerPlantCultivationInformation.setWaterAmount(375);
            flowerPlantCultivationInformation.setFertilizerAmount(2);
            flowerPlantCultivationInformation.setGrowthRate(0.5f);
            flowerPlantCultivationInformation.setHarvest(2);
            flowerPlantCultivationInformation.setUpgradeLevel(0);
            flowerPlantCultivationInformation.setVulnerability(18);
            flowerPlantCultivationInformation.setPrice(60);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.DAISY);
            flowerPlantCultivationInformation.setSoilAmount(23);
            flowerPlantCultivationInformation.setWaterAmount(237);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);
            flowerPlantCultivationInformation.setVulnerability(20);
            flowerPlantCultivationInformation.setPrice(70);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

            flowerPlantCultivationInformation = new FlowerPlantCultivationInformation();
            flowerPlantCultivationInformation.setFlowerType(FlowerType.VIOLET);
            flowerPlantCultivationInformation.setSoilAmount(27);
            flowerPlantCultivationInformation.setWaterAmount(250);
            flowerPlantCultivationInformation.setFertilizerAmount(1);
            flowerPlantCultivationInformation.setGrowthRate(0.25f);
            flowerPlantCultivationInformation.setHarvest(4);
            flowerPlantCultivationInformation.setUpgradeLevel(0);
            flowerPlantCultivationInformation.setVulnerability(90);
            flowerPlantCultivationInformation.setPrice(80);

            configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

            transaction.commit();
        }
    }

    public static void putInitialVegetablePlantCultivationInformation(ConfigService configService) {

        if (configService.readAllVegetablePlantCultivationInformation(null).size() == 0) {

            TransactionService transactionService = new TransactionServiceImpl();
            Transaction transaction = transactionService.beginTransaction(-1);

            VegetablePlantCultivationInformation vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.PEPPER);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(2);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);
            vegetablePlantCultivationInformation.setVulnerability(5);
            vegetablePlantCultivationInformation.setPrice(50);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,transaction);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.TOMATO);
            vegetablePlantCultivationInformation.setSoilAmount(25);
            vegetablePlantCultivationInformation.setWaterAmount(600);
            vegetablePlantCultivationInformation.setFertilizerAmount(2);
            vegetablePlantCultivationInformation.setGrowthRate(0.35f);
            vegetablePlantCultivationInformation.setHarvest(3);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(3);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);
            vegetablePlantCultivationInformation.setVulnerability(12);
            vegetablePlantCultivationInformation.setPrice(60);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,transaction);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.CARROT);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(2);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);
            vegetablePlantCultivationInformation.setVulnerability(20);
            vegetablePlantCultivationInformation.setPrice(70);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,transaction);

            vegetablePlantCultivationInformation = new VegetablePlantCultivationInformation();
            vegetablePlantCultivationInformation.setVegetableType(VegetableType.SALAD);
            vegetablePlantCultivationInformation.setSoilAmount(30);
            vegetablePlantCultivationInformation.setWaterAmount(450);
            vegetablePlantCultivationInformation.setFertilizerAmount(1);
            vegetablePlantCultivationInformation.setGrowthRate(0.2f);
            vegetablePlantCultivationInformation.setHarvest(6);
            vegetablePlantCultivationInformation.setRemainingNumberOfHarvests(1);
            vegetablePlantCultivationInformation.setUpgradeLevel(0);
            vegetablePlantCultivationInformation.setVulnerability(30);
            vegetablePlantCultivationInformation.setPrice(80);

            configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation,transaction);

            transaction.commit();
        }
    }
    private static void insertInitialWaterToken(){
        try {
            Connection connection = PostgresHelper.getNewConnection("water access",-1);

            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + StorageServiceImpl.STORAGE_WATER_ACCESS_TABLE);
            statement.execute("CREATE TABLE " + StorageServiceImpl.STORAGE_WATER_ACCESS_TABLE + "(data VARCHAR(1000))");

            createNotifyFunction(Arrays.asList(StorageServiceImpl.STORAGE_WATER_ACCESS_TABLE));

            statement.execute(String.format("INSERT INTO %s (data) VALUES ('{}')","wt"));

            statement.close();

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }
}
