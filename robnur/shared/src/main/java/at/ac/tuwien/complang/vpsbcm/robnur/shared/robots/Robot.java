package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import org.apache.log4j.Logger;

public abstract class Robot {

    private int id;

    final static Logger logger = Logger.getLogger(Robot.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
