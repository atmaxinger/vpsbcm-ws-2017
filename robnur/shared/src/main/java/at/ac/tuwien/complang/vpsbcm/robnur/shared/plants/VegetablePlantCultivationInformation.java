package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public class VegetablePlantCultivationInformation extends CultivationInformation {

    private VegetableType vegetableType;

    private int maxNumberOfHarvests;

    public VegetablePlantCultivationInformation() {
    }

    public VegetablePlantCultivationInformation(VegetablePlantCultivationInformation cultivationInformation) {
        super(cultivationInformation);
        vegetableType = cultivationInformation.vegetableType;
        maxNumberOfHarvests = cultivationInformation.maxNumberOfHarvests;
    }

    public VegetableType getVegetableType() {
        return vegetableType;
    }

    public void setVegetableType(VegetableType vegetableType) {
        this.vegetableType = vegetableType;
    }

    public int getRemainingNumberOfHarvests() {
        return maxNumberOfHarvests;
    }

    public void setRemainingNumberOfHarvests(int maxNumberOfHarvests) {
        this.maxNumberOfHarvests = maxNumberOfHarvests;
    }
}
