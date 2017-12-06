package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class CultivationInformation extends Idable implements Serializable {

    /* 0.2 ... 0.5 */
    private float growthRate;

    private int harvest;  // number of vegetables or flowers which can be harvested

    private int upgradeLevel;

    private int fertilizerAmount;

    private int waterAmount;

    private int soilAmount;

    private List<String> robots = new LinkedList<>();

    public CultivationInformation() {
    }

    public CultivationInformation(CultivationInformation cultivationInformation){
        growthRate = cultivationInformation.growthRate;
        harvest = cultivationInformation.harvest;
        upgradeLevel = cultivationInformation.upgradeLevel;
        fertilizerAmount = cultivationInformation.fertilizerAmount;
        waterAmount = cultivationInformation.waterAmount;
        soilAmount = cultivationInformation.soilAmount;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        if (growthRate < 0.2 || growthRate > 0.5) {
            throw new IllegalArgumentException("growthRate must be between 0.2 and 0.5");
        }

        this.growthRate = growthRate;
    }

    public int getHarvest() {
        return harvest;
    }

    public void setHarvest(int harvest) {
        this.harvest = harvest;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public int getFertilizerAmount() {
        return fertilizerAmount;
    }

    public void setFertilizerAmount(int fertilizerAmount) {
        this.fertilizerAmount = fertilizerAmount;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(int waterAmount) {
        this.waterAmount = waterAmount;
    }

    public int getSoilAmount() {
        return soilAmount;
    }

    public void setSoilAmount(int soilAmount) {
        this.soilAmount = soilAmount;
    }

    public void addResearchRobot(String id) {
        robots.add(id);
    }

    public List<String> getResearchRobots() {
        return robots;
    }
}