package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import org.apache.log4j.Logger;

public abstract class Robot {

    final static Logger logger = Logger.getLogger(Robot.class);
    private String id;

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }
}
