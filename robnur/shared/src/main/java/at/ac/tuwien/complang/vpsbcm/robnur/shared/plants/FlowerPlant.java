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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowerPlant that = (FlowerPlant) o;

        return cultivationInformation != null ? cultivationInformation.equals(that.cultivationInformation) : that.cultivationInformation == null;
    }

    @Override
    public int hashCode() {
        return cultivationInformation != null ? cultivationInformation.hashCode() : 0;
    }
}
