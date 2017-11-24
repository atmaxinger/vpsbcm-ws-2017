package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public class Flower implements Serializable {

    private FlowerPlant parentFlowerPlant;

    public FlowerPlant getParentFlowerPlant() {
        return parentFlowerPlant;
    }

    public void setParentFlowerPlant(FlowerPlant parentFlowerPlant) {
        this.parentFlowerPlant = parentFlowerPlant;
    }
}
