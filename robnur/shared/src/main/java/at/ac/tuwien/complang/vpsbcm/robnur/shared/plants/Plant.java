package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public abstract class Plant extends Idable implements Serializable {

    private String plantRobot;

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

    public abstract String getTypeName();

    public abstract CultivationInformation getCultivationInformation();
}
