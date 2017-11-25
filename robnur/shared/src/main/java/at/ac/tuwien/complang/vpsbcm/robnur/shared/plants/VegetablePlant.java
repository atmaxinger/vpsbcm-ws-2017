package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public class VegetablePlant extends Plant {

    VegetablePlantCultivationInformation cultivationInformation;

    public VegetablePlantCultivationInformation getCultivationInformation() {
        return cultivationInformation;
    }

    public void setCultivationInformation(VegetablePlantCultivationInformation cultivationInformation) {
        this.cultivationInformation = cultivationInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VegetablePlant that = (VegetablePlant) o;

        return cultivationInformation != null ? cultivationInformation.equals(that.cultivationInformation) : that.cultivationInformation == null;
    }

    @Override
    public int hashCode() {
        return cultivationInformation != null ? cultivationInformation.hashCode() : 0;
    }
}
