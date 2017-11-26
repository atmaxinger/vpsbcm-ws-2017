package at.ac.tuwien.complang.vpsbcm.robnur.shared.plants;

import java.io.Serializable;
import java.util.UUID;

public abstract class Plant extends Idable implements Serializable {


    /* 0 ... 100 */
    private int growth;

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }
}
