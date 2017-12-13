package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.postgres.robots.PostgresMonitoringRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.stream.FactoryConfigurationError;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class GreenhouseServiceImpl extends GreenhouseService {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GreenhouseServiceImpl.class);

    private static final String GREENHOUSE_FLOWER_PLANT_TABLE = "gfp";
    private static final String GREENHOUSE_VEGETABLE_PLANT_TABLE = "gvp";

    private List<Listener> listeners = new LinkedList<>();

    private boolean exit = false;

    @Override
    public boolean isExit() {
        return exit;
    }

    @Override
    public void setExit(boolean exit) {
        this.exit = exit;
        if(exit == true) {
            for(Listener listener : listeners) {
                listener.shutdown();
            }
        }
    }

    public GreenhouseServiceImpl() {

        try {
            Listener flowerListener = new Listener(GREENHOUSE_FLOWER_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if (greenhouseChanged != null) {
                        List<FlowerPlant> flowerPlants = readAllFlowerPlants();
                        List<VegetablePlant> vegetablePlants = readAllVegetablePlants();
                        List<Plant> plants = new LinkedList<>();
                        plants.addAll(flowerPlants);
                        plants.addAll(vegetablePlants);
                        greenhouseChanged.handle(plants);
                    }
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(GREENHOUSE_VEGETABLE_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if (greenhouseChanged != null) {
                        List<FlowerPlant> flowerPlants = readAllFlowerPlants();
                        List<VegetablePlant> vegetablePlants = readAllVegetablePlants();
                        List<Plant> plants = new LinkedList<>();
                        plants.addAll(flowerPlants);
                        plants.addAll(vegetablePlants);
                        greenhouseChanged.handle(plants);
                    }
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
    public boolean plantVegetables(List<VegetablePlant> vegetablePlants, Transaction transaction) {
        for(VegetablePlant vegetablePlant : vegetablePlants) {
            if(!ServiceUtil.writeItem(vegetablePlant, GREENHOUSE_VEGETABLE_PLANT_TABLE, transaction)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean plantFlowers(List<FlowerPlant> flowerPlants, Transaction transaction) {
        for(FlowerPlant flowerPlant : flowerPlants) {
            if(!ServiceUtil.writeItem(flowerPlant, GREENHOUSE_FLOWER_PLANT_TABLE, transaction)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean plant(VegetablePlant vegetablePlant, Transaction transaction) {
        /*if (readAllFlowerPlants(transaction).size() + readAllVegetablePlants(transaction).size() >= 20) {
            return false;
        }*/
        logger.debug(String.format("plant vegetable; id: %s, remainingHarvests: %d, threadid = %s, growth = %s",vegetablePlant.getId(),vegetablePlant.getCultivationInformation().getRemainingNumberOfHarvests(),Thread.currentThread().getId(), vegetablePlant.getGrowth()));
        return plantVegetables(Collections.singletonList(vegetablePlant), transaction);
    }

    @Override
    public boolean plant(FlowerPlant flowerPlant, Transaction transaction) {
        return plantFlowers(Collections.singletonList(flowerPlant), transaction);
    }

    @Override
    public List<VegetablePlant> getAllVegetablePlants(Transaction transaction) {
        List<VegetablePlant> vegetablePlants = readAllVegetablePlants(transaction);

        logger.debug(String.format("GreenhouseServiceImpl: read %d vegetable plants", vegetablePlants.size()));

        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            int cnt = statement.executeUpdate("DELETE FROM " + GREENHOUSE_VEGETABLE_PLANT_TABLE);
            logger.debug(String.format("GreenhouseServiceImpl: deleted %d vegetable plants", cnt));
        } catch (SQLException e) {
            logger.debug(String.format("GreenhouseServiceImpl: deleted did not work --> try again"));
            logger.trace("EXCEPTION", e);
            return null;
        }
        return vegetablePlants;
    }

    @Override
    public List<FlowerPlant> getAllFlowerPlants(Transaction transaction) {
        List<FlowerPlant> flowerPlants = readAllFlowerPlants(transaction);

        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            int cnt = statement.executeUpdate("DELETE FROM " + GREENHOUSE_FLOWER_PLANT_TABLE);
            logger.debug(String.format("GreenhouseServiceImpl: deleted %d flower plants", cnt));
        } catch (SQLException e) {
            logger.debug(String.format("GreenhouseServiceImpl: deleted flowerplant did not work --> try again"));
            logger.trace("EXCEPTION", e);
            return null;
        }

        return flowerPlants;
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants(Transaction transaction) {
        return ServiceUtil.readAllItems(GREENHOUSE_VEGETABLE_PLANT_TABLE, VegetablePlant.class);
    }

    @Override
    public List<FlowerPlant> readAllFlowerPlants(Transaction transaction) {
        return ServiceUtil.readAllItems(GREENHOUSE_FLOWER_PLANT_TABLE, FlowerPlant.class);
    }

    @Override
    public VegetablePlant getHarvestableVegetablePlant(Transaction transaction) {
        VegetablePlant result = null;

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.debug("getHarvestableVegetablePlant -create statement - returning null");
            logger.trace("EXCEPTION", e);
            return null;
        }

        ResultSet rs = null;
        try {
            rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data ->> 'growth')::numeric >= 100", GREENHOUSE_VEGETABLE_PLANT_TABLE));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, VegetablePlant.class);

                ServiceUtil.deleteItemById(result.getId(), GREENHOUSE_VEGETABLE_PLANT_TABLE, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            logger.fatal("getHarvestableVegetablePlant - select and delete - returning null");
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.debug("getHarvestableVegetablePlant - statement close - Ignoring");
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    @Override
    public FlowerPlant getHarvestableFlowerPlant(Transaction transaction) {
        FlowerPlant result = null;


        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.debug("getHarvestableFlowerPlant - create statement - returning null");
            logger.trace("EXCEPTION", e);
            return null;
        }

        ResultSet rs = null;
        try {
            rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data::json->>'growth')::numeric >= 100", GREENHOUSE_FLOWER_PLANT_TABLE));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, FlowerPlant.class);

                ServiceUtil.deleteItemById(result.getId(), GREENHOUSE_FLOWER_PLANT_TABLE, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            logger.debug("getHarvestableFlowerPlant - select and delete - returning null");
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.debug("getHarvestableFlowerPlant - statement close - ignoring");
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {
        try {
            Listener flowerListener = new Listener(GREENHOUSE_FLOWER_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            flowerListener.start();


            Listener vegetableListener = new Listener(GREENHOUSE_VEGETABLE_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            vegetableListener.start();

            listeners.add(flowerListener);
            listeners.add(vegetableListener);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public static List<String> getTables() {
        return Arrays.asList(GREENHOUSE_FLOWER_PLANT_TABLE, GREENHOUSE_VEGETABLE_PLANT_TABLE);
    }
}
