package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;

public class PlantPoint<P extends Plant> implements Serializable {

    private P plant;

    public P getPlant() {
        return plant;
    }

    public void setPlant(P plant) {
        this.plant = plant;
    }
}
