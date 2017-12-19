package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.List;

public abstract class EndProduct extends Idable implements Serializable{
    private String packingRobotId;
    private String deliveryRobotId;

    public String getPackingRobotId() {
        return packingRobotId;
    }

    public void setPackingRobotId(String packingRobotId) {
        this.packingRobotId = packingRobotId;
    }

    public String getDeliveryRobotId() {
        return deliveryRobotId;
    }

    public void setDeliveryRobotId(String deliveryRobotId) {
        this.deliveryRobotId = deliveryRobotId;
    }

    public abstract List<Harvestable> getParts();

    /**
     *
     * @return the price in cents
     */
    public abstract int getPrice();
}
