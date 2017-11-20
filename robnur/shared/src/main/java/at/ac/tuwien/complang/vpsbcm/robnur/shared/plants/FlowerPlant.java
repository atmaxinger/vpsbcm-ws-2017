package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public class FlowerPlant extends Plant {

    public FlowerPlantCultivationInformation cultivationInformation;

    public FlowerPlantCultivationInformation getCultivationInformation() {
        return cultivationInformation;
    }

    public void setCultivationInformation(FlowerPlantCultivationInformation cultivationInformation) {
        this.cultivationInformation = cultivationInformation;
    }
}
