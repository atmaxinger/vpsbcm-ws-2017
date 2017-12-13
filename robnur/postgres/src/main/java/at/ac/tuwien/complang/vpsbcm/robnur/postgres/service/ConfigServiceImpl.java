package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ConfigServiceImpl extends ConfigService {
    final static Logger logger = Logger.getLogger(ConfigServiceImpl.class);

    private static final String CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE = "cfpci";
    private static final String CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE = "cvpci";

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

    public ConfigServiceImpl() {
        try {
            Listener flowerPlantListener = new Listener(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowerCultivationInformationChanged(readAllFlowerPlantCultivationInformation(null));
                }
            };
            flowerPlantListener.start();

            Listener flowersListener = new Listener(CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetableCultivationInformationChanged(readAllVegetablePlantCultivationInformation(null));
                }
            };
            flowersListener.start();

            listeners.add(flowerPlantListener);
            listeners.add(flowersListener);

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(String id, Transaction transaction) {
        return ServiceUtil.getItemById(id,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    @Override
    public FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(FlowerType flowerType, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'flowerType'",flowerType.name(),CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    @Override
    public VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(VegetableType vegetableType, Transaction transaction) {
        return ServiceUtil.getItemByParameter("'vegetableType'",vegetableType.name(), CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class,transaction);
    }

    public void deleteFlowerPlantCultivationInformation(String id, Transaction transaction) {
        try {
            ServiceUtil.deleteItemById(id,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public void deleteVegetablePlantCultivationInformation(String id, Transaction transaction) {
        try {
            ServiceUtil.deleteItemById(id,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public void putFlowerPlantCultivationInformation(FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction) {
        ServiceUtil.writeItem(flowerPlantCultivationInformation,CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
    }

    public void putVegetablePlantCultivationInformation(VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction) {
        ServiceUtil.writeItem(vegetablePlantCultivationInformation,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,transaction);
    }

    public List<FlowerPlantCultivationInformation> readAllFlowerPlantCultivationInformation(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class);
        }
        return ServiceUtil.readAllItems(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,FlowerPlantCultivationInformation.class,transaction);
    }

    public List<VegetablePlantCultivationInformation> readAllVegetablePlantCultivationInformation(Transaction transaction) {
        if(transaction == null) {
            return ServiceUtil.readAllItems(CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class);
        }
        return ServiceUtil.readAllItems(CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE,VegetablePlantCultivationInformation.class,transaction);
    }

    public static List<String> getTables() {
        return Arrays.asList(CONFIG_FLOWER_PLANT_CULTIVATION_INFORMATION_TABLE,CONFIG_VEGETABLE_PLANT_CULTIVATION_INFORMATION_TABLE);
    }
}
