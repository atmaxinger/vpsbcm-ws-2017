package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionServiceImpl implements TransactionService {

    private Connection connection;

    public Transaction beginTransaction(long timeoutMillis) {
        try {
            connection = PostgresHelper.getNewConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TransactionImpl(connection);
    }
}
