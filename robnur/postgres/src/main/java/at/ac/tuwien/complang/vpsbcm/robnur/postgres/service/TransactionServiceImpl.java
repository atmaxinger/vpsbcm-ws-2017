package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class TransactionServiceImpl implements TransactionService {
    final static Logger logger = Logger.getLogger(TransactionService.class);

    public synchronized Transaction beginTransaction(long timoutMillis, String reason) {
        Connection connection = null;

        try {

            connection = PostgresHelper.getNewConnection("begin transaction, reason: " + reason, (int) timoutMillis);
            //logger.debug("NEW CONNECTION: " + connection + " reason: " + reason);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//            connection.setNetworkTimeout(Executors.newFixedThreadPool(10), Integer.MAX_VALUE);
/*            if(timeoutMillis >= 0) {
                if(timeoutMillis >= Integer.MAX_VALUE) {
                    throw new IllegalArgumentException(String.format("timeoutMillis too large (max %d)", Integer.MAX_VALUE));
                }*/
//                connection.setNetworkTimeout(Executors.newFixedThreadPool(10), (int) timeoutMillis);
            //}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //logger.debug(String.format("Started transaction on connection %s", connection));

        return new TransactionImpl(connection, reason);
    }

    public Transaction beginTransaction(long timeoutMillis) {
        return beginTransaction(timeoutMillis, "");
    }
}
