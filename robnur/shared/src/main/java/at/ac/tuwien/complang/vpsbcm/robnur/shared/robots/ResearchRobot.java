package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;

import java.util.ArrayList;
import java.util.List;

public class ResearchRobot extends Robot {

    private ResearchService researchService;
    private CompostService compostService;
    private ConfigService configService;
    private TransactionService transactionService;

    public ResearchRobot(String id, ResearchService researchService, CompostService compostService, ConfigService configService, TransactionService transactionService) {
        this.setId(id);

        this.researchService = researchService;
        this.compostService = compostService;
        this.configService = configService;
        this.transactionService = transactionService;

        tryUpgradeFlowerPlant();
        tryUpgradeVegetablePlant();
    }

    public synchronized void tryUpgradeFlowerPlant() {

        logger.info(String.format("ResearchRobot %s: looking for flowers to upgrade", this.getId()));
        // try to find a flower-plant to upgrade
        for (FlowerType flowerType : FlowerType.values()) {
            Transaction transaction = transactionService.beginTransaction(-1);
            List<Flower> flowers = researchService.getAllFlowers(transaction);

            FlowerPlantCultivationInformation cultivationInformation = configService.getFlowerPlantCultivationInformation(flowerType, transaction);
            if(cultivationInformation == null) {
                logger.fatal(String.format("CultivationInformation for %s is null!", flowerType));
                transaction.rollback();
                continue;
            }

            // check if upgrade level of flower-type is already at a maximum
            if (cultivationInformation.getUpgradeLevel() >= 5) {
                logger.debug(String.format("upgrade level of %s is already >= 5", cultivationInformation.getFlowerType()));
                transaction.rollback();
                continue;
            }

            List<Flower> upgradableFlowersOfSameType = getUpgradableFlowersOfSameType(flowerType, flowers, 12);

            // check if there are enough upgradable flowers of the same type
            if (upgradableFlowersOfSameType == null) {
                transaction.rollback();
                continue;
            }

            logger.info(String.format("ResearchRobot %s: found 12 flowers of same type (%s)", this.getId(), flowerType.name()));

            // put back non used flowers
            for (Flower f : flowers) {
                if(!upgradableFlowersOfSameType.contains(f)) {
                    researchService.putFlower(f, transaction);
                }
            }

            upgradeFlowerPlant(flowerType, cultivationInformation, transaction);

            // put the flowers on the compost
            for (Flower f : upgradableFlowersOfSameType) {
                logger.info(String.format("ResearchRobot %s: put flower(%s) on the compost", this.getId(), f.getId()));
                f.setCompostRobot(getId());
                compostService.putFlower(f, transaction);
            }

            waitResearchTime();
            transaction.commit();

            tryUpgradeFlowerPlant();
        }
    }

    public synchronized void tryUpgradeVegetablePlant() {

        logger.info(String.format("ResearchRobot %s: looking for vegetables to upgrade", this.getId()));
        // try to find a vegetable-plant to upgrade
        for (VegetableType vegetableType : VegetableType.values()) {
            Transaction transaction = transactionService.beginTransaction(-1);
            List<Vegetable> vegetables = researchService.getAllVegetables(transaction);

            VegetablePlantCultivationInformation cultivationInformation = configService.getVegetablePlantCultivationInformation(vegetableType, transaction);
            if(cultivationInformation == null) {
                logger.fatal(String.format("CultivationInformation for %s is null!", vegetableType));
                transaction.rollback();
                continue;
            }

            // check if upgrade level of vegetable-type is already at a maximum
            if (cultivationInformation.getUpgradeLevel() >= 5) {
                logger.debug(String.format("upgrade level of %s is already >= 5", cultivationInformation.getVegetableType()));
                transaction.rollback();
                continue;
            }

            List<Vegetable> upgradableVegetablesOfSameType = getUpgradableVegetablesOfSameType(vegetableType, vegetables, 12);

            // check if there are enough upgradable vegetables of the same type
            if (upgradableVegetablesOfSameType == null) {
                transaction.rollback();
                continue;
            }

            logger.info(String.format("ResearchRobot %s: found 12 vegetables of same type (%s)", this.getId(), vegetableType.name()));

            // put back not used vegetables
            for (Vegetable v : vegetables) {
                if(!upgradableVegetablesOfSameType.contains(v)) {
                    researchService.putVegetable(v, transaction);
                }
            }

            upgradeVegetablePlant(vegetableType, cultivationInformation, transaction);

            // put the vegetables on the compost
            for (Vegetable v : upgradableVegetablesOfSameType) {
                logger.info(String.format("ResearchRobot %s: put vegetable(%s) on the compost", this.getId(), v.getId()));
                v.setCompostRobot(getId());
                compostService.putVegetable(v, transaction);
            }

            waitResearchTime();
            transaction.commit();

            tryUpgradeVegetablePlant();
        }
    }

    private boolean upgradeFlowerPlant(FlowerType flowerType, FlowerPlantCultivationInformation flowerPlantCultivationInformation, Transaction transaction) {
        flowerPlantCultivationInformation = (FlowerPlantCultivationInformation) upgradeCultivationInformation(flowerPlantCultivationInformation);

        // update cultivation information
        configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

        logger.info(String.format("ResearchRobot %s: upgraded Config of %s to level %d", this.getId(), flowerType.name(), flowerPlantCultivationInformation.getUpgradeLevel()));

        return true;    // cultivation information successfully upgraded
    }

    private boolean upgradeVegetablePlant(VegetableType vegetableType, VegetablePlantCultivationInformation vegetablePlantCultivationInformation, Transaction transaction) {
        vegetablePlantCultivationInformation = (VegetablePlantCultivationInformation) upgradeCultivationInformation(vegetablePlantCultivationInformation);

        // update cultivation information
        configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation, transaction);

        logger.info(String.format("ResearchRobot %s: upgraded Config of %s to level %d", this.getId(), vegetableType.name(), vegetablePlantCultivationInformation.getUpgradeLevel()));

        return true;
    }

    private CultivationInformation upgradeCultivationInformation(CultivationInformation cultivationInformation) {

        switch (cultivationInformation.getUpgradeLevel()) {
            case 0:
                cultivationInformation.setHarvest(cultivationInformation.getHarvest() + 2);
                break;
            case 1:
                cultivationInformation.setFertilizerAmount(cultivationInformation.getFertilizerAmount() - 1);
                break;
            case 2:
                cultivationInformation.setWaterAmount(cultivationInformation.getWaterAmount() - 50);
                break;
            case 3:
                cultivationInformation.setSoilAmount(cultivationInformation.getSoilAmount() - 10);
                break;
            case 4:
                cultivationInformation.setHarvest(cultivationInformation.getHarvest() + 4);
                cultivationInformation.setGrowthRate(0.5f);
                break;
        }

        cultivationInformation.setUpgradeLevel(cultivationInformation.getUpgradeLevel() + 1);

        cultivationInformation.addResearchRobot(this.getId());

        return cultivationInformation;
    }

    List<Flower> getUpgradableFlowersOfSameType(FlowerType flowerType, List<Flower> flowers, int amount) {

        List<Flower> flowersOfSameType = new ArrayList<>();

        for (Flower f : flowers) {
            if (f.getParentFlowerPlant().getCultivationInformation().getFlowerType().equals(flowerType) &&
                    f.getParentFlowerPlant().getCultivationInformation().getUpgradeLevel() < 5) {

                flowersOfSameType.add(f);

                amount--;
                if (amount == 0) {
                    break;
                }
            }
        }

        // check if <amount> upgradable flowers were available
        if (amount != 0) {
            return null;
        }

        return flowersOfSameType;
    }

    List<Vegetable> getUpgradableVegetablesOfSameType(VegetableType vegetableType, List<Vegetable> vegetables, int amount) {

        List<Vegetable> vegetablesOfSameType = new ArrayList<>();

        for (Vegetable v : vegetables) {
            if (v.getParentVegetablePlant().getCultivationInformation().getVegetableType().equals(vegetableType) &&
                    v.getParentVegetablePlant().getCultivationInformation().getUpgradeLevel() < 5) {

                vegetablesOfSameType.add(v);

                amount--;
                if (amount == 0) {
                    break;
                }
            }
        }

        // check if <amount> upgradable flowers were available
        if (amount != 0) {
            return null;
        }


        return vegetablesOfSameType;
    }

    private void waitResearchTime() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
