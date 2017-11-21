package at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces;

import java.io.Serializable;

public class SoilPackage implements Serializable{

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
