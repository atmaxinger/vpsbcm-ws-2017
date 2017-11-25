package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.List;

public class VegetableBasket implements Serializable {

    private List<Vegetable> vegetables;

    public List<Vegetable> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<Vegetable> vegetables) {
        this.vegetables = vegetables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VegetableBasket that = (VegetableBasket) o;

        return vegetables != null ? vegetables.equals(that.vegetables) : that.vegetables == null;
    }

    @Override
    public int hashCode() {
        return vegetables != null ? vegetables.hashCode() : 0;
    }
}
