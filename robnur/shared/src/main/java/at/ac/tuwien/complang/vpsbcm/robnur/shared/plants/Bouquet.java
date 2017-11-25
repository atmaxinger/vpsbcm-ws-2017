package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.List;

public class Bouquet implements Serializable {

    private List<Flower> flowers;

    public List<Flower> getFlowers() {
        return flowers;
    }

    public void setFlowers(List<Flower> flowers) {
        this.flowers = flowers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bouquet bouquet = (Bouquet) o;

        return flowers != null ? flowers.equals(bouquet.flowers) : bouquet.flowers == null;
    }

    @Override
    public int hashCode() {
        return flowers != null ? flowers.hashCode() : 0;
    }
}
