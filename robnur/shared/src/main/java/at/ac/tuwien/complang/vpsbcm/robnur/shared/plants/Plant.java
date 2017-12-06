package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public abstract class Plant extends Idable implements Serializable {

    private String plantRobot;
    private String compostRobot;

    /* 0 ... 100 */
    private int growth;

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public String getPlantRobot() {
        return plantRobot;
    }

    public void setPlantRobot(String plantRobot) {
        this.plantRobot = plantRobot;
    }

    public String getCompostRobot() {
        return compostRobot;
    }

    public void setCompostRobot(String compostRobot) {
        this.compostRobot = compostRobot;
    }

    public abstract String getTypeName();

    public abstract CultivationInformation getCultivationInformation();
}
