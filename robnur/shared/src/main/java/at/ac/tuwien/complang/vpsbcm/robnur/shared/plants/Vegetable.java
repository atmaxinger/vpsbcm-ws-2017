package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public class Vegetable implements Serializable {

    private VegetablePlant parentVegetablePlant;

    public VegetablePlant getParentVegetablePlant() {
        return parentVegetablePlant;
    }

    public void setParentVegetablePlant(VegetablePlant parentVegetablePlant) {
        this.parentVegetablePlant = parentVegetablePlant;
    }
}
