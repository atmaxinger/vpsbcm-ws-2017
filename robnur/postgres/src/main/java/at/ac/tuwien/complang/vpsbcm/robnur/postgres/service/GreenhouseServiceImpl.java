package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.FosterRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GreenhouseServiceImpl extends GreenhouseService {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GreenhouseServiceImpl.class);

    private static final String GREENHOUSE_FLOWER_PLANT_TABLE = "gfp";
    private static final String GREENHOUSE_VEGETABLE_PLANT_TABLE = "gvp";

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
        return plantVegetables(Collections.singletonList(vegetablePlant), transaction);
    }

    @Override
    public boolean plant(FlowerPlant flowerPlant, Transaction transaction) {
        return plantFlowers(Collections.singletonList(flowerPlant), transaction);
    }

    @Override
    public List<VegetablePlant> getAllVegetablePlants(Transaction transaction) {
        List<VegetablePlant> vegetablePlants = readAllVegetablePlants(transaction);

        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            statement.executeUpdate("DELETE FROM " + GREENHOUSE_VEGETABLE_PLANT_TABLE);
        } catch (SQLException e) {
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
            statement.executeUpdate("DELETE FROM " + GREENHOUSE_FLOWER_PLANT_TABLE);
        } catch (SQLException e) {
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
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
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
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    @Override
    public VegetablePlant getLimpVegetablePlant(Transaction transaction) {
        VegetablePlant result = null;

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return null;
        }

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data::json->>'growth')::numeric = %d LIMIT 1",GREENHOUSE_VEGETABLE_PLANT_TABLE,Plant.STATUS_LIMP));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, VegetablePlant.class);

                ServiceUtil.deleteItemById(result.getId(), GREENHOUSE_VEGETABLE_PLANT_TABLE, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    @Override
    public FlowerPlant getLimpFlowerPlant(Transaction transaction) {
        FlowerPlant result = null;

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return null;
        }

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data::json->>'growth')::numeric = %d LIMIT 1",GREENHOUSE_FLOWER_PLANT_TABLE,Plant.STATUS_LIMP));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, FlowerPlant.class);

                ServiceUtil.deleteItemById(result.getId(), GREENHOUSE_FLOWER_PLANT_TABLE, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    @Override
    public FlowerPlant getInfestedFlowerPlant(Transaction transaction) {

        FlowerPlant result = null;

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return null;
        }

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data::json->>'growth')::numeric >= %d AND (data::json->>'infestation')::float >= 0.2 LIMIT 1",GREENHOUSE_FLOWER_PLANT_TABLE,Plant.STATUS_PLANTED));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, FlowerPlant.class);

                ServiceUtil.deleteItemById(result.getId(), GREENHOUSE_FLOWER_PLANT_TABLE, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    @Override
    public VegetablePlant getInfestedVegetablePlant(Transaction transaction) {

        VegetablePlant result = null;

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return null;
        }

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data::json->>'growth')::numeric >= %d AND (data::json->>'infestation')::float >= 0.2 LIMIT 1",GREENHOUSE_VEGETABLE_PLANT_TABLE,Plant.STATUS_PLANTED));

            if (rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, VegetablePlant.class);

                ServiceUtil.deleteItemById(result.getId(), GREENHOUSE_VEGETABLE_PLANT_TABLE, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {
        try {
            Listener flowerListener = new Listener(GREENHOUSE_FLOWER_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            flowerListener.start();


            Listener vegetableListener = new Listener(GREENHOUSE_VEGETABLE_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            vegetableListener.start();

            listeners.add(flowerListener);
            listeners.add(vegetableListener);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public void registerFosterRobot(FosterRobot robot) {

        try {
            Listener flowerListener = new Listener(GREENHOUSE_FLOWER_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if(method == DBMETHOD.INSERT) {
                        robot.foster();
                    }
                }
            };
            flowerListener.start();

            Listener vegetableListener = new Listener(GREENHOUSE_VEGETABLE_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if(method == DBMETHOD.INSERT) {
                        robot.foster();
                    }
                }
            };
            vegetableListener.start();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public static List<String> getTables() {
        return Arrays.asList(GREENHOUSE_FLOWER_PLANT_TABLE, GREENHOUSE_VEGETABLE_PLANT_TABLE);
    }
}
