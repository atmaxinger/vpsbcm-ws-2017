package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface TransactionService {
    Transaction beginTransaction(long timeoutMillis);
}
