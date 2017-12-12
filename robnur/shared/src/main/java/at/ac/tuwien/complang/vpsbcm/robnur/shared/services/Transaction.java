package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface Transaction {
    boolean commit();
    boolean rollback();

    boolean hasBeenRolledBack();    // TODO: never used
}
