package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Flower implements Serializable{

    public static List<Flower> fromFlowerPlant(FlowerPlant plant) {
        List<Flower> flowers = new LinkedList<>();

        for(int i=0; i < plant.getCultivationInformation().getHarvest(); i++) {
            Flower f = new Flower();
            f.setParentFlowerPlant(plant);
            flowers.add(f);
        }

        return flowers;
    }

    private FlowerPlant parentFlowerPlant;

    public FlowerPlant getParentFlowerPlant() {
        return parentFlowerPlant;
    }

    public void setParentFlowerPlant(FlowerPlant parentFlowerPlant) {
        this.parentFlowerPlant = parentFlowerPlant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flower flower = (Flower) o;

        return parentFlowerPlant != null ? parentFlowerPlant.equals(flower.parentFlowerPlant) : flower.parentFlowerPlant == null;
    }

    @Override
    public int hashCode() {
        return parentFlowerPlant != null ? parentFlowerPlant.hashCode() : 0;
    }
}
