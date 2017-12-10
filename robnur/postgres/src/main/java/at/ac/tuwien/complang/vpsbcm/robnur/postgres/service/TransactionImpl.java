package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionImpl implements Transaction {
    final static Logger logger = Logger.getLogger(TransactionService.class);

    private Connection connection;
    private boolean rolledBack = false;
    private String reason;

    public TransactionImpl(Connection connection, String reason) {
        this.connection = connection;
        this.reason = reason;
    }

    public void commit() {
        try {
            //logger.debug(String.format("trying to commit connection %s", connection));
            //logger.debug(String.format("-------- COMMIT TRANSACTION (%s) --------", reason));
            connection.commit();
            connection.close();
            //logger.debug(String.format("committed connection %s", connection));
            //connection.close();
            //logger.debug(String.format("closed connection %s after commit", connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            //logger.debug(String.format("trying to rollback connection %s", connection));
            logger.debug(String.format("------------------- ROLLBACK TRANSACTION (%s)-------------------", reason));
            connection.rollback();
            connection.close();
            //logger.debug(String.format("rolled back connection %s", connection));
            //connection.close();
            //logger.debug(String.format("closed connection %s after rollback", connection));
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