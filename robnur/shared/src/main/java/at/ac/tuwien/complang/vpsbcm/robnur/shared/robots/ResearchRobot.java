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

    public void tryUpgradeFlowerPlant() {
        Transaction transaction = transactionService.beginTransaction(-1);

        List<Flower> flowers = researchService.readAllFlowers(transaction);

        logger.info(String.format("ResearchRobot %s: looking for flowers to upgrade", this.getId()));

        // try to find a flower-plant to upgrade
        for (FlowerType flowerType : FlowerType.values()) {

            // check if upgrade level of flower-type is already at a maximum
            if (configService.readFlowerPlantCultivationInformation(flowerType, transaction).getUpgradeLevel() >= 5) {
                transaction.commit();
                return;
            }

            List<Flower> upgradableFlowersOfSameType = getUpgradableFlowersOfSameType(flowerType, flowers, 12);

            // check if there are enough upgradable flowers of the same type
            if (upgradableFlowersOfSameType == null) {
                continue;
            }

            logger.info(String.format("ResearchRobot %s: found 12 flowers of same type (%s)", this.getId(), flowerType.name()));

            // remove the flowers from the research-queue
            for (Flower f : upgradableFlowersOfSameType) {
                logger.info(String.format("ResearchRobot %s: remove flower(%s) form research-queue", this.getId(), f.getId()));
                researchService.deleteFlower(f, transaction);
            }

            transaction.commit();

            if (!upgradeFlowerPlant(flowerType)) {
                // cultivation information was updated to level 5 (max level) by another research
                for (Flower f : upgradableFlowersOfSameType) {
                    logger.info(String.format("ResearchRobot %s: put back flower(%s) to research-queue", this.getId(), f.getId()));
                    researchService.putFlower(f);
                }

            }else {
                // put the flowers on the compost
                for (Flower f : upgradableFlowersOfSameType) {
                    logger.info(String.format("ResearchRobot %s: put flower(%s) on the compost", this.getId(), f.getId()));
                    compostService.putFlower(f);
                }

                tryUpgradeFlowerPlant();  // try to upgrade another flower plant
            }
            return;
        }

        logger.info(String.format("ResearchRobot %s: did not find (enough) upgradeable flowers", this.getId()));
        transaction.commit();
    }

    public void tryUpgradeVegetablePlant() {
        Transaction transaction = transactionService.beginTransaction(-1);

        List<Vegetable> vegetables = researchService.readAllVegetables(transaction);

        logger.info(String.format("ResearchRobot %s: looking for vegetables to upgrade", this.getId()));
        // try to find a vegetable-plant to upgrade
        for (VegetableType vegetableType : VegetableType.values()) {

            // check if upgrade level of flower-type is already at a maximum
            if (configService.readVegetablePlantCultivationInformation(vegetableType, transaction).getUpgradeLevel() >= 5) {
                return;
            }

            List<Vegetable> upgradableVegetablesOfSameType = getUpgradableVegetablesOfSameType(vegetableType, vegetables, 12);

            // check if there are enough upgradable vegetables of the same type
            if (upgradableVegetablesOfSameType == null) {
                transaction.commit();
                continue;
            }

            logger.info(String.format("ResearchRobot %s: found 12 vegetables of same type (%s)", this.getId(), vegetableType.name()));

            // remove the vegetables from the research-queue
            for (Vegetable v : upgradableVegetablesOfSameType) {
                logger.info(String.format("ResearchRobot %s: remove vegetable(%s) form research-queue", this.getId(), v.getId()));
                researchService.putVegetable(v);
            }

            transaction.commit();

            if (!upgradeVegetablePlant(vegetableType)) {
                // cultivation information was updated to level 5 (max level) by another research
                for (Vegetable v : upgradableVegetablesOfSameType) {
                    logger.info(String.format("ResearchRobot %s: put vegetable(%s) back in research-queue", this.getId(), v.getId()));
                    researchService.putVegetable(v);
                }
                return;

            }else {
                // put the flowers on the compost
                for (Vegetable v : upgradableVegetablesOfSameType) {
                    logger.info(String.format("ResearchRobot %s: put vegetable(%s) on the compost", this.getId(), v.getId()));
                    compostService.putVegetable(v);
                }

                tryUpgradeVegetablePlant();  // try to upgrade another plant
            }

            return;
        }

        logger.info(String.format("ResearchRobot %s: did not find (enough) upgradeable vegetables", this.getId()));
        transaction.commit();
    }

    private boolean upgradeFlowerPlant(FlowerType flowerType) {
        Transaction transaction = transactionService.beginTransaction(-1);

        FlowerPlantCultivationInformation flowerPlantCultivationInformation = configService.readFlowerPlantCultivationInformation(flowerType,transaction);

        if (flowerPlantCultivationInformation.getUpgradeLevel() >= 5) {
            // maximum upgrade level reached
            return false;
        }

        flowerPlantCultivationInformation = (FlowerPlantCultivationInformation) upgradeCultivationInformation(flowerPlantCultivationInformation);

        // update cultivation information
        configService.deleteFlowerPlantCultivationInformation(flowerPlantCultivationInformation.getId(), transaction);
        configService.putFlowerPlantCultivationInformation(flowerPlantCultivationInformation, transaction);

        logger.info(String.format("ResearchRobot %s: upgraded Config of %s to level %d", this.getId(), flowerType.name(), flowerPlantCultivationInformation.getUpgradeLevel()));

        waitResearchTime();

        transaction.commit();

        return true;    // cultivation information successfully upgraded
    }

    private boolean upgradeVegetablePlant(VegetableType vegetableType) {
        Transaction transaction = transactionService.beginTransaction(-1);

        VegetablePlantCultivationInformation vegetablePlantCultivationInformation = configService.readVegetablePlantCultivationInformation(vegetableType,transaction);

        if (vegetablePlantCultivationInformation.getUpgradeLevel() >= 5) {
            // maximum upgrade level reached
            return false;
        }

        vegetablePlantCultivationInformation = (VegetablePlantCultivationInformation) upgradeCultivationInformation(vegetablePlantCultivationInformation);

        // update cultivation information
        configService.deleteVegetablePlantCultivationInformation(vegetablePlantCultivationInformation.getId(), transaction);
        configService.putVegetablePlantCultivationInformation(vegetablePlantCultivationInformation, transaction);

        logger.info(String.format("ResearchRobot %s: upgraded Config of %s to level %d", this.getId(), vegetableType.name(), vegetablePlantCultivationInformation.getUpgradeLevel()));

        waitResearchTime();
        transaction.commit();

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

    private FlowerPlantCultivationInformation getFlowerPlantCultivationInformation(FlowerType flowerType, Transaction transaction) {
        List<FlowerPlantCultivationInformation> flowerPlantCultivationInformation = configService.readAllFlowerPlantCultivationInformation(transaction);

        for (FlowerPlantCultivationInformation cultivationInformation : flowerPlantCultivationInformation) {
            if (cultivationInformation.getFlowerType().equals(flowerType)) {
                if (cultivationInformation.getUpgradeLevel() >= 5) {
                    return null;
                }
                return cultivationInformation;
            }
        }

        return null;
    }

    private VegetablePlantCultivationInformation getVegetablePlantCultivationInformation(VegetableType vegetableType, Transaction transaction) {

        List<VegetablePlantCultivationInformation> vegetablePlantCultivationInformation = configService.readAllVegetablePlantCultivationInformation(transaction);

        for (VegetablePlantCultivationInformation cultivationInformation : vegetablePlantCultivationInformation) {
            if (cultivationInformation.getVegetableType().equals(vegetableType)) {
                if (cultivationInformation.getUpgradeLevel() >= 5) {
                    return null;
                }
                return cultivationInformation;
            }
        }
        return null;
    }

}
