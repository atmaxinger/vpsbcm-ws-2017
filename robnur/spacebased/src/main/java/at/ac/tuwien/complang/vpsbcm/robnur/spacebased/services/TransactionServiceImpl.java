package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import org.mozartspaces.core.*;

import java.net.URI;

public class TransactionServiceImpl implements TransactionService {

    public static TransactionReference getTransactionReference(Transaction transaction) {
        if(transaction != null && transaction instanceof TransactionImpl) {
            return ((TransactionImpl) transaction).ref;
        }

        return null;
    }

    class TransactionImpl implements Transaction {
        TransactionReference ref;
        Capi capi;

        private boolean rolledBack = false;

        @Override
        public void commit() {
            try {
                capi.commitTransaction(ref);
            } catch (MzsCoreException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void rollback() {
            try {
                capi.rollbackTransaction(ref);
                rolledBack = true;
            } catch (MzsCoreException e) {
                e.printStackTrace();
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
    public Transaction beginTransaction(long timeoutMillis) {
        if(timeoutMillis < 0) {
            timeoutMillis = MzsConstants.TransactionTimeout.INFINITE;
        }
        try {
            TransactionReference ref = capi.createTransaction(timeoutMillis, spaceUri);

            TransactionImpl transaction = new TransactionImpl();
            transaction.capi = this.capi;
            transaction.ref = ref;

            return transaction;
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }
}
