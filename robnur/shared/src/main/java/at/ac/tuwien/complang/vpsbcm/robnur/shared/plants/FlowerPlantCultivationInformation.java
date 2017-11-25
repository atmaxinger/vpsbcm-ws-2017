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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowerPlantCultivationInformation that = (FlowerPlantCultivationInformation) o;

        return flowerType == that.flowerType;
    }

    @Override
    public int hashCode() {
        return flowerType != null ? flowerType.hashCode() : 0;
    }
}
