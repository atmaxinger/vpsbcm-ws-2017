package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface Transaction {
    void commit();
    void rollback();

    boolean hasBeenRolledBack();    // TODO: never used
}
