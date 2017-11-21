package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import org.mozartspaces.capi3.Capi3AspectPort;
import org.mozartspaces.capi3.SubTransaction;
import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.TakeEntriesRequest;

import java.io.Serializable;
import java.util.List;

public class WaterAspect extends AbstractContainerAspect {

    private Capi capi;

    public WaterAspect(Capi capi) {
        this.capi = capi;
    }

    @Override
    public AspectResult postTake(TakeEntriesRequest<?> request,
                                 Transaction tx,
                                 SubTransaction stx,
                                 Capi3AspectPort capi3,
                                 int executionCount,
                                 List<Serializable> entries) {


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Water water = new Water();
        water.setAmount(250);

        try {
            capi.write(request.getContainer(), new Entry(water));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return AspectResult.OK;
    }
}
