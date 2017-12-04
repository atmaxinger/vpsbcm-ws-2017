package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

class TransactionImpl implements Transaction {

    private Connection connection;
    private boolean rolledBack = false;

    public TransactionImpl(Connection connection) {
        this.connection = connection;
    }

    public void commit() {
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.close();
            rolledBack = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasBeenRolledBack() {
        return rolledBack;
    }

    public Connection getConnection() {
        return connection;
    }
}