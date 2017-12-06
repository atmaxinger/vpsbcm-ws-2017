package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public abstract class Harvestable extends Idable implements Serializable {
    private String harvestRobot;
    private String compostRobot;

    public String getCompostRobot() {
        return compostRobot;
    }

    public void setCompostRobot(String compostRobot) {
        this.compostRobot = compostRobot;
    }

    public String getHarvestRobot() {
        return harvestRobot;
    }

    public void setHarvestRobot(String harvestRobot) {
        this.harvestRobot = harvestRobot;
    }

    public abstract Plant getParentPlant();
}
