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

    public ResearchRobot(ResearchService researchService, CompostService compostService, ConfigService configService, TransactionService transactionService) {
        this.researchService = researchService;
        this.compostService = compostService;
        this.configService = configService;
        this.transactionService = transactionService;
    }

    public void tryUpgradePlant(){
        Transaction transaction = transactionService.beginTransaction(-1);

        List<Flower> flowers = researchService.readAllFlowers(transaction);
        List<Vegetable> vegetables = researchService.readAllVegetables(transaction);

        // try to find a flower-plant to upgrade
        for (FlowerType flowerType:FlowerType.values()) {
            List<Flower> flowersOfSameType = getFlowersOfSameType(flowerType,flowers);

            if(flowersOfSameType.size() >= 12){
                upgradeFlowerPlant(flowerType,transaction);

                for(int i = 0; i < 12; i++)
                {
                    if(getMaxFlowerPlantUpgradeLevel(flowers) < 5) {
                        researchService.deleteFlower(flowersOfSameType.get(i),transaction);
                        compostService.putFlower(flowersOfSameType.get(i));
                    }
                }

                transaction.commit();
                tryUpgradePlant();  // try to upgrade another plant
                return;
            }
        }

        // try to find a vegetable-plant to upgrade
        for (VegetableType vegetableType:VegetableType.values()) {
            List<Vegetable> vegetablesOfSameType = getVegetablesOfSameType(vegetableType,vegetables);
            if(vegetablesOfSameType.size() >= 12){
                upgradeVegetablePlant(vegetableType,transaction);

                for(int i = 0; i < 12; i++)
                {
                    if(getMaxVegetablePlantUpgradeLevel(vegetables) < 5) {
                        researchService.deleteVegetable(vegetables.get(i),transaction);
                        compostService.putVegetable(vegetablesOfSameType.get(i));
                    }
                }

                transaction.commit();
                tryUpgradePlant();  // try to upgrade another plant
                return;
            }
        }
    }

    private void upgradeFlowerPlant(FlowerType flowerType, Transaction transaction) {
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
    }

    private void upgradeVegetablePlant(VegetableType vegetableType, Transaction transaction){
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

        return cultivationInformation;
    }

    List<Flower> getFlowersOfSameType(FlowerType flowerType, List<Flower> flowers){

        List<Flower> flowersOfSameType = new ArrayList<>();

        for (Flower f:flowers) {
            if (f.getParentFlowerPlant().getCultivationInformation().getFlowerType().equals(flowerType)){
                flowersOfSameType.add(f);
            }
        }

        return flowersOfSameType;
    }

    List<Vegetable> getVegetablesOfSameType(VegetableType vegetableType, List<Vegetable> vegetables){

        List<Vegetable> vegetablesOfSameType = new ArrayList<>();

        for (Vegetable v:vegetables) {
            if (v.getParentVegetablePlant().getCultivationInformation().getVegetableType().equals(vegetableType)){
                vegetablesOfSameType.add(v);
            }
        }

        return vegetablesOfSameType;
    }

    private int getMaxFlowerPlantUpgradeLevel(List<Flower> flowers){

        int maxUpgradeLevel = 0;

        for (Flower f:flowers) {
            int upgradeLevel = f.getParentFlowerPlant().getCultivationInformation().getUpgradeLevel();

            if(upgradeLevel > maxUpgradeLevel){
                maxUpgradeLevel = upgradeLevel;
            }
        }

        return maxUpgradeLevel;
    }

    private int getMaxVegetablePlantUpgradeLevel(List<Vegetable> vegetables){

        int maxUpgradeLevel = 0;

        for (Vegetable v:vegetables) {
            int upgradeLevel = v.getParentVegetablePlant().getCultivationInformation().getUpgradeLevel();

            if(upgradeLevel > maxUpgradeLevel){
                maxUpgradeLevel = upgradeLevel;
            }
        }

        return maxUpgradeLevel;
    }
}
