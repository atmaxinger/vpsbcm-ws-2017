package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.List;

public abstract class EndProduct extends Idable implements Serializable{
    private String packingRobotId;

    public String getPackingRobotId() {
        return packingRobotId;
    }

    public void setPackingRobotId(String packingRobotId) {
        this.packingRobotId = packingRobotId;
    }

    public abstract List<Harvestable> getParts();
}
