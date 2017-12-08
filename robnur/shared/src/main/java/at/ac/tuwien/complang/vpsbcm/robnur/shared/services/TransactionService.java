package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface TransactionService {
    Transaction beginTransaction(long timeoutMillis);
    Transaction beginTransaction(long timeoutMillis, String reason);
}
