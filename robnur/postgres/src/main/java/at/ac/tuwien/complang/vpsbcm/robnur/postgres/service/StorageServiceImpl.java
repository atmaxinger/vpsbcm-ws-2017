package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.FosterRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;
import org.postgresql.PGConnection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StorageServiceImpl extends StorageService {

    private static final String STORAGE_FLOWER_SEED_TABLE = "sfs";
    private static final String STORAGE_VEGETABLE_SEED_TABLE = "svs";
    private static final String STORAGE_SOIL_TABLE = "ss";
    private static final String STORAGE_FLOWER_FERTILIZER_TABLE = "sff";
    private static final String STORAGE_VEGETABLE_FERTILIZER_TABLE = "svf";
    private static final String STORAGE_WATER_TABLE = "sw";
    private static final String STORAGE_WATER_TOKEN_TABLE = "wt";
    private static final String STORAGE_FLOWER_PESTICIDE_TABLE = "fp";
    private static final String STORAGE_VEGETABLE_PESTICIDE_TABLE = "vp";
    public static final String STORAGE_WATER_ACCESS_TABLE = "wa";

    final static Logger logger = Logger.getLogger(StorageServiceImpl.class);

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

    public StorageServiceImpl() {
        try {
            Listener flowerSeedListener = new Listener(STORAGE_FLOWER_SEED_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                   notifyFlowerSeedsChanged(readAllFlowerSeeds());
                }
            };
            flowerSeedListener.start();

            Listener vegetableSeedListener = new Listener(STORAGE_VEGETABLE_SEED_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetableSeedsChanged(readAllVegetableSeeds());
                }
            };
            vegetableSeedListener.start();

            Listener soilListener = new Listener(STORAGE_SOIL_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifySoilPackagesChanged(readAllSoilPackage());
                }
            };
            soilListener.start();

            Listener flowerFertilizerListener = new Listener(STORAGE_FLOWER_FERTILIZER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowerFertilizerChanged(readAllFlowerFertilizer());
                }
            };
            flowerFertilizerListener.start();

            Listener vegetableFertilizerListener = new Listener(STORAGE_VEGETABLE_FERTILIZER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetableFertilizerChanged(readAllVegetableFertilizer());
                }
            };
            vegetableFertilizerListener.start();

            Listener accessWaterListener = new Listener(STORAGE_WATER_ACCESS_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if(method == DBMETHOD.INSERT){
                        try {
                            Connection connection = PostgresHelper.getNewConnection("water access",-1);
                            connection.setAutoCommit(true);
                            Statement statement = connection.createStatement();

                            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + STORAGE_WATER_ACCESS_TABLE);
                            resultSet.next();
                            String robotId = resultSet.getString("data");

                            notifyWaterRobotChanged(robotId);

                        } catch (SQLException e) {
                            logger.trace("EXCEPTION", e);
                        }
                    } else if(method == DBMETHOD.DELETE){
                        notifyWaterRobotChanged(null);
                    }
                }
            };
            accessWaterListener.start();


            Listener flowerPesticideListener = new Listener(STORAGE_FLOWER_PESTICIDE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowerPesticidesChanged(readAllFlowerPesticides(null));
                }
            };
            flowerPesticideListener.start();

            Listener vegetablePesticideListener = new Listener(STORAGE_VEGETABLE_PESTICIDE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetablePesticidesChanged(readAllVegetablePesticides(null));
                }
            };
            vegetablePesticideListener.start();


            listeners.add(flowerSeedListener);
            listeners.add(vegetableSeedListener);
            listeners.add(flowerFertilizerListener);
            listeners.add(vegetableSeedListener);
            listeners.add(soilListener);
            listeners.add(accessWaterListener);
            listeners.add(flowerPesticideListener);
            listeners.add(vegetablePesticideListener);

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    protected FlowerPlant getSeed(FlowerType type, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'cultivationInformation'->>'flowerType'",type.name(),STORAGE_FLOWER_SEED_TABLE,FlowerPlant.class,transaction);
    }

    @Override
    protected VegetablePlant getSeed(VegetableType type, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'cultivationInformation'->>'vegetableType'",type.name(),STORAGE_VEGETABLE_SEED_TABLE,VegetablePlant.class,transaction);
    }

    @Override
    public void putSeed(FlowerPlant plant, Transaction transaction) {
        ServiceUtil.writeItem(plant,STORAGE_FLOWER_SEED_TABLE,transaction);
    }

    @Override
    public void putSeed(VegetablePlant plant, Transaction transaction) {
        ServiceUtil.writeItem(plant,STORAGE_VEGETABLE_SEED_TABLE,transaction);
    }

    @Override
    public void putFlowerSeeds(List<FlowerPlant> plants, Transaction transaction) {
        for (FlowerPlant fp:plants) {
            putSeed(fp,transaction);
        }
    }

    @Override
    public void putVegetableSeeds(List<VegetablePlant> plants, Transaction transaction) {
        for (VegetablePlant vp:plants) {
            putSeed(vp,transaction);
        }
    }

    @Override
    public List<FlowerPlant> readAllFlowerSeeds(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(STORAGE_FLOWER_SEED_TABLE, FlowerPlant.class);
        }

        return ServiceUtil.readAllItems(STORAGE_FLOWER_SEED_TABLE,FlowerPlant.class,transaction);
    }

    @Override
    public List<VegetablePlant> readAllVegetableSeeds(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(STORAGE_VEGETABLE_SEED_TABLE, VegetablePlant.class);
        }

        return ServiceUtil.readAllItems(STORAGE_VEGETABLE_SEED_TABLE,VegetablePlant.class,transaction);
    }

    @Override
    protected List<SoilPackage> getAllSoilPackages(Transaction transaction) {
        List<SoilPackage> soilPackages = ServiceUtil.getAllItems(STORAGE_SOIL_TABLE,SoilPackage.class,transaction);
        return soilPackages;
    }

    @Override
    public void putSoilPackage(SoilPackage soilPackage, Transaction transaction) {
        ServiceUtil.writeItem(soilPackage,STORAGE_SOIL_TABLE,transaction);
    }

    @Override
    public void putSoilPackages(List<SoilPackage> soilPackages, Transaction transaction) {
        for (SoilPackage sp:soilPackages) {
            putSoilPackage(sp,transaction);
        }
    }

    @Override
    public List<SoilPackage> readAllSoilPackage(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(STORAGE_SOIL_TABLE,SoilPackage.class);
        }
        return ServiceUtil.readAllItems(STORAGE_SOIL_TABLE,SoilPackage.class,transaction);
    }

    @Override
    public List<FlowerFertilizer> getFlowerFertilizer(int amount, Transaction transaction) {
        List<FlowerFertilizer> flowerFertilizers = ServiceUtil.readAllItems(STORAGE_FLOWER_FERTILIZER_TABLE,FlowerFertilizer.class,transaction);

        if(flowerFertilizers.size()<amount){
            return null;
        }

        List<FlowerFertilizer> result = new ArrayList<>();

        for (int i = 0; i<amount; i++){
            FlowerFertilizer flowerFertilizer = flowerFertilizers.get(i);
            result.add(flowerFertilizer);
            try {
                ServiceUtil.deleteItemById(flowerFertilizer.getId(),STORAGE_FLOWER_FERTILIZER_TABLE,transaction);
            } catch (SQLException e) {
                logger.trace("EXCEPTION", e);
                return null;
            }
        }
        return result;
    }

    @Override
    public void putFlowerFertilizer(FlowerFertilizer flowerFertilizer) {
        ServiceUtil.writeItem(flowerFertilizer,STORAGE_FLOWER_FERTILIZER_TABLE);
    }

    @Override
    public void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers) {
        for (FlowerFertilizer ff:flowerFertilizers) {
            putFlowerFertilizer(ff);
        }
    }

    @Override
    public void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers, Transaction t) {
        for(FlowerFertilizer ff : flowerFertilizers) {
            ServiceUtil.writeItem(ff, STORAGE_FLOWER_FERTILIZER_TABLE, t);
        }
    }

    @Override
    public List<FlowerFertilizer> readAllFlowerFertilizer(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(STORAGE_FLOWER_FERTILIZER_TABLE,FlowerFertilizer.class);
        }
        return ServiceUtil.readAllItems(STORAGE_FLOWER_FERTILIZER_TABLE,FlowerFertilizer.class, transaction);
    }

    @Override
    public List<VegetableFertilizer> getVegetableFertilizer(int amount, Transaction transaction) {
        List<VegetableFertilizer> vegetableFertilizers = ServiceUtil.readAllItems(STORAGE_VEGETABLE_FERTILIZER_TABLE,VegetableFertilizer.class,transaction);

        if(vegetableFertilizers.size()<amount){
            return null;
        }

        List<VegetableFertilizer> result = new ArrayList<>();

        for (int i = 0; i<amount; i++){
            VegetableFertilizer vegetableFertilizer = vegetableFertilizers.get(i);
            result.add(vegetableFertilizer);
            try {
                ServiceUtil.deleteItemById(vegetableFertilizer.getId(),STORAGE_VEGETABLE_FERTILIZER_TABLE,transaction);
            } catch (SQLException e) {
                logger.trace("EXCEPTION", e);
                return null;
            }
        }
        return result;
    }

    @Override
    public void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer) {
        ServiceUtil.writeItem(vegetableFertilizer,STORAGE_VEGETABLE_FERTILIZER_TABLE);
    }

    @Override
    public void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers) {
        for (VegetableFertilizer vf:vegetableFertilizers) {
            putVegetableFertilizer(vf);
        }
    }

    @Override
    public void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers, Transaction t) {
        for(VegetableFertilizer vf : vegetableFertilizers) {
            ServiceUtil.writeItem(vf, STORAGE_VEGETABLE_FERTILIZER_TABLE, t);
        }
    }

    @Override
    public List<VegetableFertilizer> readAllVegetableFertilizer(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(STORAGE_VEGETABLE_FERTILIZER_TABLE,VegetableFertilizer.class);
        }
        return ServiceUtil.readAllItems(STORAGE_VEGETABLE_FERTILIZER_TABLE,VegetableFertilizer.class,transaction);
    }

    @Override
    public void putFlowerPesticides(List<FlowerPesticide> pesticides, Transaction transaction) {
        for (FlowerPesticide fp:pesticides) {
            ServiceUtil.writeItem(fp,STORAGE_FLOWER_PESTICIDE_TABLE,transaction);
        }
    }

    @Override
    public FlowerPesticide getFlowerPesticide(Transaction transaction) {
        return ServiceUtil.getItem(STORAGE_FLOWER_PESTICIDE_TABLE,FlowerPesticide.class,transaction);
    }

    @Override
    public List<FlowerPesticide> readAllFlowerPesticides(Transaction transaction) {
        if(transaction != null){
            return ServiceUtil.readAllItems(STORAGE_FLOWER_PESTICIDE_TABLE,FlowerPesticide.class,transaction);
        } else {
            return ServiceUtil.readAllItems(STORAGE_FLOWER_PESTICIDE_TABLE,FlowerPesticide.class);
        }
    }

    @Override
    public void putVegetablePesticides(List<VegetablePesticide> pesticides, Transaction transaction) {
        for (VegetablePesticide vp:pesticides) {
            ServiceUtil.writeItem(vp,STORAGE_VEGETABLE_PESTICIDE_TABLE,transaction);
        }
    }

    @Override
    public VegetablePesticide getVegetablePesticide(Transaction transaction) {
        return ServiceUtil.getItem(STORAGE_VEGETABLE_PESTICIDE_TABLE,VegetablePesticide.class,transaction);
    }

    @Override
    public List<VegetablePesticide> readAllVegetablePesticides(Transaction transaction) {
        if(transaction != null) {
            return ServiceUtil.readAllItems(STORAGE_VEGETABLE_PESTICIDE_TABLE, VegetablePesticide.class, transaction);
        }else {
            return ServiceUtil.readAllItems(STORAGE_VEGETABLE_PESTICIDE_TABLE, VegetablePesticide.class);
        }
    }

    @Override
    public Water accessTap(String robotId) {

        try {
            Connection connection = PostgresHelper.getNewConnection("water access",-1);
            Statement statement = connection.createStatement();

            boolean locked = true;
            while (locked){
                connection.setAutoCommit(false);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + STORAGE_WATER_TOKEN_TABLE);
                if(resultSet.next()){
                    statement.execute("DELETE FROM " + STORAGE_WATER_TOKEN_TABLE);
                    connection.commit();
                    locked = false;
                } else {
                    connection.commit();
                    Thread.sleep(100);
                }
            }

            connection.setAutoCommit(true);

            statement.execute(String.format("INSERT INTO %s (data) VALUES ('%s')",STORAGE_WATER_ACCESS_TABLE,robotId));

            Thread.sleep(1000);
            Water water = new Water();
            water.setAmount(250);

            statement.execute("DELETE FROM " + STORAGE_WATER_ACCESS_TABLE);

            statement.execute(String.format("INSERT INTO %s (data) VALUES ('{}')",STORAGE_WATER_TOKEN_TABLE));

            return water;

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }

        return null;
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {

        try {

            Listener flowerSeedListener = new Listener(STORAGE_FLOWER_SEED_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            flowerSeedListener.start();

            Listener vegetableSeedListener = new Listener(STORAGE_VEGETABLE_SEED_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            vegetableSeedListener.start();

            Listener soilListener = new Listener(STORAGE_SOIL_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            soilListener.start();

            Listener flowerFertilizerListener = new Listener(STORAGE_FLOWER_FERTILIZER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            flowerFertilizerListener.start();

            Listener vegetableVerbalizeListener = new Listener(STORAGE_VEGETABLE_FERTILIZER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            vegetableVerbalizeListener.start();

            Listener waterListener = new Listener(STORAGE_WATER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.doStuff();
                }
            };
            waterListener.start();

            listeners.add(flowerSeedListener);
            listeners.add(vegetableSeedListener);
            listeners.add(flowerFertilizerListener);
            listeners.add(vegetableVerbalizeListener);
            listeners.add(soilListener);
            listeners.add(waterListener);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public void registerFosterRobot(FosterRobot robot) {

        try {
            Listener flowerPesticideListener = new Listener(STORAGE_FLOWER_PESTICIDE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if(method == DBMETHOD.INSERT) {
                        robot.foster();
                    }
                }
            };

            flowerPesticideListener.start();

            Listener vegetablePesticideListener = new Listener(STORAGE_VEGETABLE_PESTICIDE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    if(method == DBMETHOD.INSERT) {
                        robot.foster();
                    }
                }
            };

            vegetablePesticideListener.start();

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public static List<String> getTables() {
        return Arrays.asList(STORAGE_FLOWER_SEED_TABLE,STORAGE_VEGETABLE_SEED_TABLE,STORAGE_SOIL_TABLE,STORAGE_FLOWER_FERTILIZER_TABLE,STORAGE_VEGETABLE_FERTILIZER_TABLE,STORAGE_WATER_TABLE,STORAGE_WATER_TOKEN_TABLE,STORAGE_VEGETABLE_PESTICIDE_TABLE,STORAGE_FLOWER_PESTICIDE_TABLE);
    }
}
