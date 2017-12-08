package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

class TransactionImpl implements Transaction {
    final static Logger logger = Logger.getLogger(TransactionService.class);

    private Connection connection;
    private boolean rolledBack = false;

    public TransactionImpl(Connection connection) {
        this.connection = connection;
    }

    public void commit() {
        try {
            //logger.debug(String.format("trying to commit connection %s", connection));
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
            //System.err.println("------------------- ROLLBACK TRANSACTION -------------------");
            connection.rollback();
            connection.close();
            //logger.debug(String.format("rolled back connection %s", connection));
            //connection.close();
            logger.debug(String.format("closed connection %s after rollback", connection));
            rolledBack = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable
    {
        /* try { connection.close();
            logger.debug(String.format("closed connection %s in finalizer", connection));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }*/
        super.finalize();
    }

    public boolean hasBeenRolledBack() {
        return rolledBack;
    }

    public Connection getConnection() {
        return connection;
    }
}