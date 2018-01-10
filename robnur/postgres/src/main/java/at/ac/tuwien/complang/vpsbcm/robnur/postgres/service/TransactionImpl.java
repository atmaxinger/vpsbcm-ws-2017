package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionImpl implements Transaction {
    final static Logger logger = Logger.getLogger(TransactionService.class);

    private Connection connection;
    private String reason;

    public TransactionImpl(Connection connection, String reason) {
        this.connection = connection;
        this.reason = reason;
    }

    public TransactionImpl(Connection connection) {
        this.connection = connection;
        this.reason = "";
    }

    public boolean commit() {
        try {
            logger.debug(String.format("COMMIT TRANSACTION (%s)", reason));
            connection.commit();
            connection.close();
            return true;
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }
    }

    public boolean rollback() {
        try {
            logger.debug(String.format("------------------- ROLLBACK TRANSACTION (%s)-------------------", reason));
            connection.rollback();
            connection.close();
            return true;
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}