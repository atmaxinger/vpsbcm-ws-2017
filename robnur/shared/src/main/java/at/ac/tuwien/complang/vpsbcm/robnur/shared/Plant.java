package at.ac.tuwien.complang.vpsbcm.robnur.shared;

public class Plant {
    /* 0 ... 100 */
    private int growth;
    /* 0.2 ... 0.5 */
    private float growthRate;

    private float harvest;

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        if (growthRate < 0.2 || growthRate > 0.5) {
            throw new IllegalArgumentException("growthRate must be between 0.2 and 0.5");
        }

        this.growthRate = growthRate;
    }

    public float getHarvest() {
        return harvest;
    }

    public void setHarvest(float harvest) {
        this.harvest = harvest;
    }
}
