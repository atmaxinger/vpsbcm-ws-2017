package at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Idable;

import java.io.Serializable;

public class SoilPackage extends Idable implements Serializable {

    private int amount;

    public SoilPackage() {
        this.amount = 50;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
