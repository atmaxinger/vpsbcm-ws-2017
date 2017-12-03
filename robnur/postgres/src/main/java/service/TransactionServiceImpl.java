package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionServiceImpl implements TransactionService {

    private Connection connection;

    public TransactionServiceImpl(Connection connection) {
        this.connection = connection;
    }

    public Transaction beginTransaction(long timeoutMillis) {
        return new TransactionImpl(connection);
    }

    class TransactionImpl implements Transaction {

        private Connection connection;
        private boolean rolledBack = false;

        public TransactionImpl(Connection connection) {
            this.connection = connection;
        }

        public void commit() {
            try {
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void rollback() {
            try {
                connection.rollback();
                rolledBack = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public boolean hasBeenRolledBack() {
            return rolledBack;
        }
    }
}
