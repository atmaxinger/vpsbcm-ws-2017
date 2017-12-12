package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.robots.PlantAndHarvestRobot;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageServiceImpl extends StorageService {

    private static final String STORAGE_FLOWER_SEED_TABLE = "sfs";
    private static final String STORAGE_VEGETABLE_SEED_TABLE = "svs";
    private static final String STORAGE_SOIL_TABLE = "ss";
    private static final String STORAGE_FLOWER_FERTILIZER_TABLE = "sff";
    private static final String STORAGE_VEGETABLE_FERTILIZER_TABLE = "svf";
    private static final String STORAGE_WATER_TABLE = "sw";


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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<FlowerPlant> getSeeds(FlowerType type, Transaction transaction) {
        return ServiceUtil.getItemsByParameter("'cultivationInformation'->>'flowerType'",type.name(),STORAGE_FLOWER_SEED_TABLE,FlowerPlant.class,transaction);
    }

    @Override
    protected List<VegetablePlant> getSeeds(VegetableType type, Transaction transaction) {
        return ServiceUtil.getItemsByParameter("'cultivationInformation'->>'vegetableType'",type.name(),STORAGE_VEGETABLE_SEED_TABLE,VegetablePlant.class,transaction);

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
                e.printStackTrace();
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
                e.printStackTrace();
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
    public Water accessTap() {
        // TODO: !!!!!! NOT TRANSACTIONAL SECURE !!!!!!
        // TODO: !!!!!! BUSY WAITING !!!!!!
        while (true){
            List<Water> waterList = ServiceUtil.readAllItems(STORAGE_WATER_TABLE,Water.class);
            if(waterList.size() >= 1){
                try {
                    ServiceUtil.deleteItemById(waterList.get(0).getId(),STORAGE_WATER_TABLE);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return waterList.get(0);
            }
        }
    }

    @Override
    public void putWater(Water water) {
        ServiceUtil.writeItem(water,STORAGE_WATER_TABLE);
    }

    public void registerPlantAndHarvestRobot(PlantAndHarvestRobot robot) {

        try {

            Listener flowerSeedListener = new Listener(STORAGE_FLOWER_SEED_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            flowerSeedListener.start();

            Listener vegetableSeedListener = new Listener(STORAGE_VEGETABLE_SEED_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            vegetableSeedListener.start();

            Listener soilListener = new Listener(STORAGE_SOIL_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            soilListener.start();

            Listener flowerFertilizerListener = new Listener(STORAGE_FLOWER_FERTILIZER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            flowerFertilizerListener.start();

            Listener vegetableVerbalizeListener = new Listener(STORAGE_VEGETABLE_FERTILIZER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            vegetableVerbalizeListener.start();

            Listener waterListener = new Listener(STORAGE_WATER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    robot.tryHarvestPlant();
                    robot.tryPlant();
                }
            };
            waterListener.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTables() {
        return Arrays.asList(STORAGE_FLOWER_SEED_TABLE,STORAGE_VEGETABLE_SEED_TABLE,STORAGE_SOIL_TABLE,STORAGE_FLOWER_FERTILIZER_TABLE,STORAGE_VEGETABLE_FERTILIZER_TABLE,STORAGE_WATER_TABLE);
    }
}
