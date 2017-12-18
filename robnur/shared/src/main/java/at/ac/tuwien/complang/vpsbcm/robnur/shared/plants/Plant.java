package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public abstract class Plant extends Idable implements Serializable {

    public static final int STATUS_PLANTED = -1;
    public static final int STATUS_LIMP = -10;

    private String plantRobot;
    private String compostRobot;

    /* 0 ... 100 */
    /* -1 ... angepflanzt */
    /* 100 ... erntebereit */
    /* -10 .. welk */
    private int growth;

    private float infestation = 0;

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

    public float getInfestation() {
        return infestation;
    }

    public void setInfestation(float infestation) {
        this.infestation = infestation;
    }

    public abstract String getTypeName();

    public abstract CultivationInformation getCultivationInformation();
}
