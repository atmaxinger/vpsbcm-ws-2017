package at.ac.tuwien.complang.vpsbcm.robnur.shared.services;

public interface TranscationService {
    Transaction beginTransaction(long timeoutMillis);
}
