package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.apache.log4j.Logger;
import org.mozartspaces.core.*;

import java.net.URI;

public class TransactionServiceImpl implements TransactionService {
    final static Logger logger = Logger.getLogger(TransactionService.class);
    final static Logger loggerTransaction = Logger.getLogger(TransactionImpl.class);

    public synchronized static TransactionReference getTransactionReference(Transaction transaction) {
        if(transaction != null && transaction instanceof TransactionImpl) {
            return ((TransactionImpl) transaction).ref;
        }

        return null;
    }

    public synchronized static void setTransactionTimedOut(Transaction transaction) {
        if(transaction != null) {
            logger.error(String.format("Transaction %s timed out", ((TransactionImpl)transaction).ref.getId()));

            ((TransactionImpl) transaction).setTimeOut(true);
        }
    }

    class TransactionImpl implements Transaction {

        TransactionReference ref;
        Capi capi;

        private boolean rolledBack = false;

        private boolean timeOut = false;

        public void setTimeOut(boolean timeOut) {
            this.timeOut = timeOut;
        }

        @Override
        public void commit() {
            if(!timeOut) {
                try {
                    loggerTransaction.debug(String.format("Trying to commit transaction %s", ref.getId()));
                    capi.commitTransaction(ref);
                    loggerTransaction.debug(String.format("Committed transaction %s", ref.getId()));
                } catch (MzsCoreException e) {
                    logger.debug(String.format("Error committing transaction %s", ref.getId()));
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void rollback() {
            if(!timeOut) {
                try {
                    loggerTransaction.debug(String.format("Trying to roll back transaction %s", ref.getId()));
                    capi.rollbackTransaction(ref);
                    rolledBack = true;
                    loggerTransaction.debug(String.format("Rolled back transaction %s", ref.getId()));

                } catch (MzsCoreException e) {
                    logger.debug(String.format("Error rolling back transaction %s", ref.getId()));

                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean hasBeenRolledBack() {
            return rolledBack;
        }
    }

    Capi capi;
    URI spaceUri;

    public TransactionServiceImpl(URI spaceUri) {
        this.spaceUri = spaceUri;

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);
    }

    @Override
    public synchronized Transaction beginTransaction(long timeoutMillis) {
        return beginTransaction(timeoutMillis, "");
    }

    @Override
    public synchronized Transaction beginTransaction(long timeoutMillis, String reason) {
        if(timeoutMillis < 0) {
            timeoutMillis = MzsConstants.TransactionTimeout.INFINITE;
        }
        try {
            TransactionReference ref = capi.createTransaction(timeoutMillis, spaceUri);

            TransactionImpl transaction = new TransactionImpl();
            transaction.capi = this.capi;
            transaction.ref = ref;

            logger.debug(String.format("Started transaction %s for %s", ref.getId(), reason));


            return transaction;
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }
}
