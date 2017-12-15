package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface Transaction {
    /**
     * commit the transaction
     * @return true if successful, false otherwise
     */
    boolean commit();

    /**
     * roll back the transaction
     * @return true if successful, false otherwise
     */
    boolean rollback();
}
