package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class TransactionServiceImpl implements TransactionService {

    private Connection connection;

    public Transaction beginTransaction(long timeoutMillis) {
        try {
            connection = PostgresHelper.getNewConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            if(timeoutMillis >= 0) {
                if(timeoutMillis >= Integer.MAX_VALUE) {
                    throw new IllegalArgumentException(String.format("timeoutMillis too large (max %d)", Integer.MAX_VALUE));
                }
                connection.setNetworkTimeout(Executors.newFixedThreadPool(10), (int) timeoutMillis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TransactionImpl(connection);
    }
}
