package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public class VegetablePlantCultivationInformation extends CultivationInformation {

    private VegetableType vegetableType;

    private int maxNumberOfHarvests;

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

    public int getMaxNumberOfHarvests() {
        return maxNumberOfHarvests;
    }

    public void setMaxNumberOfHarvests(int maxNumberOfHarvests) {
        this.maxNumberOfHarvests = maxNumberOfHarvests;
    }
}
