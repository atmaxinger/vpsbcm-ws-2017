package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class Harvestable extends Idable implements Serializable {
    private String harvestRobot;
    private String compostRobot;
    private List<String> putResearchRobots = new LinkedList<>();

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

    public List<String> getPutResearchRobots() {
        return putResearchRobots;
    }

    public void addPutResearchRobot(String putResearchRobot) {
        this.putResearchRobots.add(putResearchRobot);
    }

    public abstract Plant getParentPlant();
}
