package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Vegetable implements Serializable {

    private VegetablePlant parentVegetablePlant;

    public VegetablePlant getParentVegetablePlant() {
        return parentVegetablePlant;
    }

    public static List<Vegetable> fromVegetablePlant(VegetablePlant plant) {
        List<Vegetable> vegs = new LinkedList<>();

        if(plant.getCultivationInformation().getRemainingNumberOfHarvests() > 0) {
            for(int i=0; i < plant.getCultivationInformation().getHarvest(); i++) {
                Vegetable v = new Vegetable();
                v.setParentVegetablePlant(plant);
                vegs.add(v);
            }

            plant.getCultivationInformation()
                    .setRemainingNumberOfHarvests(
                            plant.getCultivationInformation()
                                    .getRemainingNumberOfHarvests()-1);
            plant.setGrowth(40);
        }

        return vegs;
    }

    public void setParentVegetablePlant(VegetablePlant parentVegetablePlant) {
        this.parentVegetablePlant = parentVegetablePlant;
    }
}
