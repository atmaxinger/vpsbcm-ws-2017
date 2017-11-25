package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Vegetable implements Serializable{

    private VegetablePlant parentVegetablePlant;

    public VegetablePlant getParentVegetablePlant() {
        return parentVegetablePlant;
    }

    public static List<Vegetable> harvestVegetablesFormPlant(VegetablePlant plant) {
        List<Vegetable> vegetables = new LinkedList<>();

        if(plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {

            for(int i=0; i < plant.getCultivationInformation().getHarvest(); i++) {
                Vegetable vegetable = new Vegetable();
                vegetable.setParentVegetablePlant(plant);
                vegetables.add(vegetable);
            }

            plant.getCultivationInformation()
                    .setRemainingNumberOfHarvests(
                            plant.getCultivationInformation()
                                    .getRemainingNumberOfHarvests()-1);

            plant.setGrowth(40);
        }

        return vegetables;
    }

    public void setParentVegetablePlant(VegetablePlant parentVegetablePlant) {
        this.parentVegetablePlant = parentVegetablePlant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vegetable vegetable = (Vegetable) o;

        return parentVegetablePlant != null ? parentVegetablePlant.equals(vegetable.parentVegetablePlant) : vegetable.parentVegetablePlant == null;
    }

    @Override
    public int hashCode() {
        return parentVegetablePlant != null ? parentVegetablePlant.hashCode() : 0;
    }
}
