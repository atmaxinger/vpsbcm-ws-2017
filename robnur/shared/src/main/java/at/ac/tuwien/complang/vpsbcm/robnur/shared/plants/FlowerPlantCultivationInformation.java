package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public class FlowerPlantCultivationInformation extends CultivationInformation {

    private FlowerType flowerType;

    public FlowerPlantCultivationInformation() {
    }

    public FlowerPlantCultivationInformation(FlowerPlantCultivationInformation cultivationInformation) {
        super(cultivationInformation);
        flowerType = cultivationInformation.flowerType;
    }

    public FlowerType getFlowerType() {
        return flowerType;
    }

    public void setFlowerType(FlowerType flowerType) {
        this.flowerType = flowerType;
    }
}
