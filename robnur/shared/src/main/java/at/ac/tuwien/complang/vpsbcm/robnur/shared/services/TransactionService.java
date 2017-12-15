package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface TransactionService {
    /**
     * begin a new transaction
     * @param timeoutMillis timeout in milliseconds, -1 if infinite
     * @return a transaction object
     */
    Transaction beginTransaction(long timeoutMillis);

    /**
     * begin a new transaction
     * @param timeoutMillis timeout in milliseconds, -1 if infinite
     * @param reason what this transaction is for (for debug purposes)
     * @return a transaction object
     */
    Transaction beginTransaction(long timeoutMillis, String reason);
}
