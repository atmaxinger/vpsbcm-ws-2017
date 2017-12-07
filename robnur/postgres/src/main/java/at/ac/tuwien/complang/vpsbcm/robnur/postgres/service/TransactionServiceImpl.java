package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class TransactionServiceImpl implements TransactionService {
    final static Logger logger = Logger.getLogger(TransactionService.class);

    private Connection connection = null;

    public Transaction beginTransaction(long timeoutMillis) {
        try {
            if(connection == null || connection.isClosed()) {
                if(connection == null) {
                    //logger.debug("Connection was null");
                }
                else if(connection.isClosed()) {
                    //logger.debug("Connection was closed");
                }
                connection = PostgresHelper.getNewConnection();
                //logger.debug(String.format("Created new connection %s", connection));
            }
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setNetworkTimeout(Executors.newFixedThreadPool(10), Integer.MAX_VALUE);
            if(timeoutMillis >= 0) {
                if(timeoutMillis >= Integer.MAX_VALUE) {
                    throw new IllegalArgumentException(String.format("timeoutMillis too large (max %d)", Integer.MAX_VALUE));
                }
                connection.setNetworkTimeout(Executors.newFixedThreadPool(10), (int) timeoutMillis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //logger.debug(String.format("Started transaction on connection %s", connection));

        return new TransactionImpl(connection);
    }
}
