package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GreenhouseServiceImpl extends GreenhouseService {

    private static final String GREENHOUSE_FLOWER_PLANT_TABLE = "gfp";
    private static final String GREENHOUSE_VEGETABLE_PLANT_TABLE = "gvp";

    public GreenhouseServiceImpl() {
        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                String table = ServiceUtil.getTableName(channelName, payload);
                switch (table) {
                    case GREENHOUSE_FLOWER_PLANT_TABLE:
                    case GREENHOUSE_VEGETABLE_PLANT_TABLE:

                        if(greenhouseChanged != null) {
                            List<FlowerPlant> flowerPlants = readAllFlowerPlants();
                            List<VegetablePlant> vegetablePlants = readAllVegetablePlants();
                            List<Plant> plants = new LinkedList<>();
                            plants.addAll(flowerPlants);
                            plants.addAll(vegetablePlants);

                            greenhouseChanged.handle(plants);
                        }

                        break;
                }

            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(GREENHOUSE_FLOWER_PLANT_TABLE);
        PostgresHelper.setUpListen(GREENHOUSE_VEGETABLE_PLANT_TABLE);
    }

    @Override
    public boolean plant(VegetablePlant vegetablePlant, Transaction transaction) {
        if(readAllFlowerPlants(transaction).size() + readAllVegetablePlants(transaction).size() >= 20){
            return false;
        }
        ServiceUtil.writeItem(vegetablePlant,GREENHOUSE_VEGETABLE_PLANT_TABLE,transaction);
        return true;
    }

    @Override
    public boolean plant(FlowerPlant flowerPlant, Transaction transaction) {
        if(readAllFlowerPlants(transaction).size() + readAllVegetablePlants(transaction).size() >= 20){
            return false;
        }
        ServiceUtil.writeItem(flowerPlant,GREENHOUSE_FLOWER_PLANT_TABLE,transaction);
        return true;
    }

    @Override
    public List<VegetablePlant> getAllVegetablePlants(Transaction transaction) {
        List<VegetablePlant> vegetablePlants = readAllVegetablePlants(transaction);
        for (VegetablePlant vp:vegetablePlants) {
            ServiceUtil.deleteItemById(vp.getId(),GREENHOUSE_VEGETABLE_PLANT_TABLE);
        }
        return vegetablePlants;
    }

    @Override
    public List<FlowerPlant> getAllFlowerPlants(Transaction transaction) {
        List<FlowerPlant> flowerPlants = readAllFlowerPlants(transaction);
        for (FlowerPlant fp:flowerPlants) {
            ServiceUtil.deleteItemById(fp.getId(),GREENHOUSE_FLOWER_PLANT_TABLE);
        }
        return flowerPlants;
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants(Transaction transaction) {
        return ServiceUtil.readAllItems(GREENHOUSE_VEGETABLE_PLANT_TABLE,VegetablePlant.class);
    }

    @Override
    public List<FlowerPlant> readAllFlowerPlants(Transaction transaction) {
        return ServiceUtil.readAllItems(GREENHOUSE_FLOWER_PLANT_TABLE,FlowerPlant.class);
    }

    @Override
    public VegetablePlant getHarvestableVegetablePlant(Transaction transaction) {
        VegetablePlant result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data ->> 'growth')::numeric >= 100", GREENHOUSE_VEGETABLE_PLANT_TABLE));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, VegetablePlant.class);

                ServiceUtil.deleteItemById(result.getId(),GREENHOUSE_VEGETABLE_PLANT_TABLE,transaction);
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public FlowerPlant getHarvestableFlowerPlant(Transaction transaction) {
        FlowerPlant result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data::json->>'growth')::numeric >= 100", GREENHOUSE_FLOWER_PLANT_TABLE));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, FlowerPlant.class);

                ServiceUtil.deleteItemById(result.getId(),GREENHOUSE_FLOWER_PLANT_TABLE,transaction);
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {

        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                String table = ServiceUtil.getTableName(channelName, payload);
                if(ServiceUtil.getOperation(channelName, payload) == ServiceUtil.DBOPERATION.INSERT) {
                    switch (table) {
                        case GREENHOUSE_FLOWER_PLANT_TABLE:
                        case GREENHOUSE_VEGETABLE_PLANT_TABLE:
                            robot.tryHarvestPlant();
                            robot.tryPlant();
                            break;
                    }
                }
            }
        };

        PostgresHelper.getConnection().addNotificationListener(listener);

        PostgresHelper.setUpListen(GREENHOUSE_FLOWER_PLANT_TABLE);
        PostgresHelper.setUpListen(GREENHOUSE_VEGETABLE_PLANT_TABLE);
    }

    public static List<String> getTables() {
        return Arrays.asList(GREENHOUSE_FLOWER_PLANT_TABLE,GREENHOUSE_VEGETABLE_PLANT_TABLE);
    }
}
