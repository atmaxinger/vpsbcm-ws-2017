package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public class FlowerPlant extends Plant implements Serializable {

    public FlowerPlantCultivationInformation cultivationInformation;

    public FlowerPlantCultivationInformation getCultivationInformation() {
        return cultivationInformation;
    }

    public void setCultivationInformation(FlowerPlantCultivationInformation cultivationInformation) {
        this.cultivationInformation = cultivationInformation;
    }

    @Override
    public String getTypeName() {
        return getCultivationInformation().getFlowerType().toString();
    }
}
