package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Bouquet extends EndProduct implements Serializable {

    private List<Flower> flowers;

    public List<Flower> getFlowers() {
        return flowers;
    }

    public void setFlowers(List<Flower> flowers) {
        this.flowers = flowers;
    }

    @Override
    public List<Harvestable> getParts() {
        return new LinkedList<>(getFlowers());
    }
}
