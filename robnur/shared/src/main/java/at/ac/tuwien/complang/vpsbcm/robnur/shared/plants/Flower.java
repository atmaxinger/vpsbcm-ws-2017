package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Flower extends Idable {

    private FlowerPlant parentFlowerPlant;

    public FlowerPlant getParentFlowerPlant() {
        return parentFlowerPlant;
    }

    public void setParentFlowerPlant(FlowerPlant parentFlowerPlant) {
        this.parentFlowerPlant = parentFlowerPlant;
    }

    public static List<Flower> harvestFlowerFromFlowerPlant(FlowerPlant plant) {
        List<Flower> flowers = new LinkedList<>();

        for(int i=0; i < plant.getCultivationInformation().getHarvest(); i++) {
            Flower f = new Flower();
            f.setParentFlowerPlant(plant);
            flowers.add(f);
        }

        return flowers;
    }
}
