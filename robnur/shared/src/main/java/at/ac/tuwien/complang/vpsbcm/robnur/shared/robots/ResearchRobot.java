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

        // try to find a flower-plant to upgrade
        for (FlowerType flowerType : FlowerType.values()) {
            logger.info(String.format("ResearchRobot %s: looking for flowers to upgrade", this.getId()));
            List<Flower> upgradableFlowersOfSameType = getUpgradableFlowersOfSameType(flowerType, flowers, 12);

            // check if there are enough upgradable flowers of the same type
            if (upgradableFlowersOfSameType == null) {
                logger.info(String.format("ResearchRobot %s: did not find (enough) upgradeable flowers", this.getId()));
                return;
            }

            logger.info(String.format("ResearchRobot %s: found 12 flowers of same type (%s)", this.getId(), flowerType.name()));

            // remove the flowers from the research-queue
            for (Flower f : upgradableFlowersOfSameType) {
                logger.info(String.format("ResearchRobot %s: remove flower(%s) form research-queue",this.getId(), f.getId()));
                researchService.deleteFlower(f, transaction);
            }

            transaction.commit();

            upgradeFlowerPlant(flowerType);

            // put the flowers on the compost
            for (Flower f : upgradableFlowersOfSameType) {
                logger.info(String.format("ResearchRobot %s: put flower(%s) on the compost",this.getId(), f.getId()));
                compostService.putFlower(f);
            }

            tryUpgradeFlowerPlant();  // try to upgrade another flower plant
            return;
        }
    }

    public void tryUpgradeVegetablePlant() {
        Transaction transaction = transactionService.beginTransaction(-1);

        List<Vegetable> vegetables = researchService.readAllVegetables(transaction);

        // try to find a vegetable-plant to upgrade
        for (VegetableType vegetableType:VegetableType.values()) {
            logger.info(String.format("ResearchRobot %s: looking for vegetables to upgrade", this.getId()));
            List<Vegetable> upgradableVegetablesOfSameType = getUpgradableVegetablesOfSameType(vegetableType,vegetables,12);

            // check if there are enough upgradable vegetables of the same type
            if(upgradableVegetablesOfSameType == null){
                logger.info(String.format("ResearchRobot %s: did not find (enough) upgradeable vegetables", this.getId()));
                return;
            }

            logger.info(String.format("ResearchRobot %s: found 12 vegetables of same type (%s)", this.getId(), vegetableType.name()));

            // remove the vegetables from the research-queue
            for(Vegetable v : upgradableVegetablesOfSameType)
            {
                logger.info(String.format("ResearchRobot %s: remove vegetable(%s) form research-queue",this.getId(), v.getId()));
                researchService.deleteVegetable(v,transaction);
            }

            transaction.commit();

            upgradeVegetablePlant(vegetableType);

            // put the flowers on the compost
            for(Vegetable v : upgradableVegetablesOfSameType)
            {
                logger.info(String.format("ResearchRobot %s: put vegetable(%s) on the compost",this.getId(), v.getId()));
                compostService.putVegetable(v);
            }

            tryUpgradeVegetablePlant();  // try to upgrade another plant
            return;
        }
    }

    private void upgradeFlowerPlant(FlowerType flowerType) {
        Transaction transaction = transactionService.beginTransaction(-1);

        List<FlowerPlantCultivationInformation> flowerPlantCultivationInformation = configService.readAllFlowerPlantCultivationInformation(transaction);
        FlowerPlantCultivationInformation cultivationInformationOfFlowerType = null;

        for (FlowerPlantCultivationInformation cultivationInformation:flowerPlantCultivationInformation) {
            if(cultivationInformation.getFlowerType().equals(flowerType)){
                cultivationInformationOfFlowerType = cultivationInformation;
                break;
            }
        }

        cultivationInformationOfFlowerType = (FlowerPlantCultivationInformation) upgradeCultivationInformation(cultivationInformationOfFlowerType);


        configService.deleteFlowerPlantCultivationInformation(cultivationInformationOfFlowerType.getId(),transaction);
        configService.putFlowerPlantCultivationInformation(cultivationInformationOfFlowerType,transaction);

        logger.info(String.format("ResearchRobot %s: upgraded Config of %s to level %d",this.getId(), flowerType.name(), cultivationInformationOfFlowerType.getUpgradeLevel()));

        waitResearchTime();
        transaction.commit();
    }

    private void upgradeVegetablePlant(VegetableType vegetableType){
        Transaction transaction = transactionService.beginTransaction(-1);

        List<VegetablePlantCultivationInformation> vegetablePlantCultivationInformation = configService.readAllVegetablePlantCultivationInformation(transaction);
        VegetablePlantCultivationInformation cultivationInformationOfVegetableType = null;

        for (VegetablePlantCultivationInformation cultivationInformation:vegetablePlantCultivationInformation) {
            if(cultivationInformation.getVegetableType().equals(vegetableType)){
                cultivationInformationOfVegetableType = cultivationInformation;
                break;
            }
        }

        cultivationInformationOfVegetableType = (VegetablePlantCultivationInformation) upgradeCultivationInformation(cultivationInformationOfVegetableType);

        configService.deleteVegetablePlantCultivationInformation(cultivationInformationOfVegetableType.getId(),transaction);
        configService.putVegetablePlantCultivationInformation(cultivationInformationOfVegetableType,transaction);

        logger.info(String.format("ResearchRobot %s: upgraded Config of %s to level %d",this.getId(), vegetableType.name(), cultivationInformationOfVegetableType.getUpgradeLevel()));

        waitResearchTime();
        transaction.commit();
    }

    private CultivationInformation upgradeCultivationInformation(CultivationInformation cultivationInformation){

        cultivationInformation.setUpgradeLevel(cultivationInformation.getUpgradeLevel());

        switch (cultivationInformation.getUpgradeLevel()){
            case 1:
                cultivationInformation.setGrowthRate(cultivationInformation.getGrowthRate() + 0.25f);
                break;
            case 2:
                cultivationInformation.setFertilizerAmount(cultivationInformation.getFertilizerAmount() - 1);
                break;
            case 3:
                cultivationInformation.setWaterAmount(cultivationInformation.getWaterAmount() - 50);
                break;
            case 4:
                cultivationInformation.setGrowthRate(cultivationInformation.getSoilAmount() - 10);
                break;
            case 5:
                cultivationInformation.setGrowthRate(cultivationInformation.getGrowthRate() + 0.5f);
                break;
        }

        cultivationInformation.addResearchRobot(this.getId());

        return cultivationInformation;
    }

    List<Flower> getUpgradableFlowersOfSameType(FlowerType flowerType, List<Flower> flowers, int amount){

        List<Flower> flowersOfSameType = new ArrayList<>();

        for (Flower f:flowers) {
            if (f.getParentFlowerPlant().getCultivationInformation().getFlowerType().equals(flowerType) &&
                    f.getParentFlowerPlant().getCultivationInformation().getUpgradeLevel() < 5){

                flowersOfSameType.add(f);

                amount --;
                if(amount == 0){
                    break;
                }
            }
        }

        // check if <amount> upgradable flowers were available
        if(amount != 0){
            return null;
        }

        return flowersOfSameType;
    }

    List<Vegetable> getUpgradableVegetablesOfSameType(VegetableType vegetableType, List<Vegetable> vegetables, int amount){

        List<Vegetable> vegetablesOfSameType = new ArrayList<>();

        for (Vegetable v:vegetables) {
            if (v.getParentVegetablePlant().getCultivationInformation().getVegetableType().equals(vegetableType) &&
                    v.getParentVegetablePlant().getCultivationInformation().getUpgradeLevel() < 5){

                vegetablesOfSameType.add(v);

                amount --;
                if(amount == 0){
                    break;
                }
            }
        }

        // check if <amount> upgradable flowers were available
        if(amount != 0){
            return null;
        }


        return vegetablesOfSameType;
    }

    private void waitResearchTime(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
