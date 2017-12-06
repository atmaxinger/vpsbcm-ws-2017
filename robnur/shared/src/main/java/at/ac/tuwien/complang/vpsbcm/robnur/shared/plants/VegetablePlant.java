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
    public String getTypeName() {
        return getCultivationInformation().getVegetableType().toString();
    }
}
