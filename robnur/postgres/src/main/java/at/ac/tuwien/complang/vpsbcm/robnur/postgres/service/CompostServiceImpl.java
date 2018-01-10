package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CompostServiceImpl extends CompostService {
    final static Logger logger = Logger.getLogger(CompostServiceImpl.class);

    private static final String COMPOST_FLOWER_PLANT_TABLE = "cfp";
    private static final String COMPOST_VEGETABLE_PLANT_TABLE = "cvp";
    private static final String COMPOST_FLOWER_TABLE = "cf";
    private static final String COMPOST_VEGETABLE_TABLE = "cv";

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

    public CompostServiceImpl() {
        try {
            Listener flowerPlantListener = new Listener(COMPOST_FLOWER_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowerPlantsChanged(readAllFlowerPlants());
                }
            };
            flowerPlantListener.start();

            Listener flowersListener = new Listener(COMPOST_FLOWER_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyFlowersChanged(readAllFlowers());
                }
            };
            flowersListener.start();

            Listener vegetablePlantListener = new Listener(COMPOST_VEGETABLE_PLANT_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetablePlantsChanged(readAllVegetablePlants());
                }
            };
            vegetablePlantListener.start();

            Listener vegetablesListener = new Listener(COMPOST_VEGETABLE_TABLE) {
                @Override
                public void onNotify(int pid, DBMETHOD method) {
                    notifyVegetablesChanged(readAllVegetables());
                }
            };
            vegetablesListener.start();

            listeners.add(flowerPlantListener);
            listeners.add(flowersListener);
            listeners.add(vegetablePlantListener);
            listeners.add(vegetablesListener);

        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    @Override
    public void putFlowerPlant(FlowerPlant flowerPlant, Transaction transaction) {
        ServiceUtil.writeItem(flowerPlant, COMPOST_FLOWER_PLANT_TABLE, transaction);
    }

    @Override
    public void putVegetablePlant(VegetablePlant vegetablePlant, Transaction transaction) {
        ServiceUtil.writeItem(vegetablePlant, COMPOST_VEGETABLE_PLANT_TABLE, transaction);
    }

    @Override
    public void putFlower(Flower flower, Transaction transaction) {
        ServiceUtil.writeItem(flower, COMPOST_FLOWER_TABLE, transaction);
    }

    @Override
    public void putVegetable(Vegetable vegetable, Transaction transaction) {
        ServiceUtil.writeItem(vegetable, COMPOST_VEGETABLE_TABLE, transaction);
    }

    @Override
    public List<FlowerPlant> readAllFlowerPlants() {
        return ServiceUtil.readAllItems(COMPOST_FLOWER_PLANT_TABLE,FlowerPlant.class);
    }

    @Override
    public List<VegetablePlant> readAllVegetablePlants() {
        return ServiceUtil.readAllItems(COMPOST_VEGETABLE_PLANT_TABLE,VegetablePlant.class);
    }

    @Override
    public List<Flower> readAllFlowers() {
        return ServiceUtil.readAllItems(COMPOST_FLOWER_TABLE,Flower.class);
    }

    @Override
    public List<Vegetable> readAllVegetables() {
        return ServiceUtil.readAllItems(COMPOST_VEGETABLE_TABLE,Vegetable.class);
    }

    public static List<String> getTables(){
        return Arrays.asList(COMPOST_FLOWER_PLANT_TABLE,COMPOST_FLOWER_TABLE,COMPOST_VEGETABLE_PLANT_TABLE,COMPOST_VEGETABLE_TABLE);
    }
}
