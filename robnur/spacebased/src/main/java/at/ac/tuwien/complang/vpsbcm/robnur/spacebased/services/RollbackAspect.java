package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AbstractSpaceAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.RollbackTransactionRequest;

import java.io.Serializable;

public class RollbackAspect extends AbstractSpaceAspect {

    public interface RollbackCallback extends Serializable { void rolledBack(); }

    private Runnable callbackRunner = new Runnable() {
        @Override
        public void run() {
            callback.rolledBack();
        }
    };

    private RollbackCallback callback;

    public RollbackAspect(RollbackCallback callback) {
        this.callback = callback;
    }

    @Override
    public AspectResult postRollbackTransaction(RollbackTransactionRequest request, Transaction tx) {
        Thread t = new Thread(callbackRunner);
        t.start();

        return AspectResult.OK;
    }
}
