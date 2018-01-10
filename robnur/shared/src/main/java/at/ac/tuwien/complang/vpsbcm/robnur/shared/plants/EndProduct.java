package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class EndProduct extends Idable implements Serializable{
    private List<String> packingRobotIds = new ArrayList<>();
    private String deliveryRobotId;

    private String formatList(List<String> list) {
        String s="";

        for(int i=0; i<list.size(); i++) {
            s += list.get(i);
            if(i < list.size()-1) {
                s += ", ";
            }
        }

        return s;
    }

    public String getPackingRobotIdsAsString() {
        return formatList(packingRobotIds);
    }

    public List<String> getPackingRobotIds() {
        return packingRobotIds;
    }

    public void setPackingRobotId(String packingRobotId) {
        packingRobotIds.add(packingRobotId);
    }

    public void setPackingRobotIds(List<String> packingRobotIds) {
        this.packingRobotIds.addAll(packingRobotIds);
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
