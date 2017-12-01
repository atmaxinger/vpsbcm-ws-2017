package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import org.apache.log4j.Logger;
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
    final static Logger logger = Logger.getLogger(WaterAspect.class);

    private final int WATER_INTERVAL_MS = 1000;

    @Override
    public AspectResult postTake(TakeEntriesRequest<?> request,
                                 Transaction tx,
                                 SubTransaction stx,
                                 Capi3AspectPort capi3,
                                 int executionCount,
                                 List<Serializable> entries) {

        logger.debug("Someone has taken water - wait " + WATER_INTERVAL_MS + " ms");

        try {
            Thread.sleep(WATER_INTERVAL_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Water water = new Water();
        water.setAmount(250);

        try {
            logger.debug("trying to put new water");

            new Capi(getCore()).write(request.getContainer(), new Entry(water));

            logger.debug("put new water");

        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return AspectResult.OK;
    }


}
