package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class VegetableBasket extends EndProduct implements Serializable {

    private List<Vegetable> vegetables;

    public List<Vegetable> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<Vegetable> vegetables) {
        this.vegetables = vegetables;
    }

    @JsonIgnore
    @Override
    public List<Harvestable> getParts() {
        return new LinkedList<>(getVegetables());
    }
}
