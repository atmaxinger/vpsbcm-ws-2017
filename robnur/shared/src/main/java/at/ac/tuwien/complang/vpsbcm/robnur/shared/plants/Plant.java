package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

public abstract class Plant {

    /* 0 ... 100 */
    private int growth;

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }
}
