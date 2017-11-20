package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public class PlantPoint<P extends Plant> {

    private P plant;

    public P getPlant() {
        return plant;
    }

    public void setPlant(P plant) {
        this.plant = plant;
    }
}
