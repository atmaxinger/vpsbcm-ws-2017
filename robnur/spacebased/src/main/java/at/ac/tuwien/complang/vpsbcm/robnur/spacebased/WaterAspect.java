package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import org.apache.log4j.Logger;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.TakeEntriesRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mozartspaces.core.MzsConstants.Selecting.COUNT_MAX;

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

 /*       boolean tokenWasTaken = false;

        for(Selector selector : request.getSelectors()) {
            if(selector instanceof LabelCoordinator.LabelSelector) {
                if(((LabelCoordinator.LabelSelector) selector).getLabel().equals(StorageServiceImpl.WATER_TOKEN_LABEL)) {
                    tokenWasTaken = true;
                }
            }
        }

        if(!tokenWasTaken) {
            return AspectResult.OK;
        }

        Capi capi = new Capi(getCore());
        String robotId = "UNKNOWN";
        Selector accessorSelector = LabelCoordinator.newSelector(StorageServiceImpl.WATER_ACCESSOR_LABEL, COUNT_MAX);

        try {

             ArrayList<String> ids = capi.read(request.getContainer(), accessorSelector, MzsConstants.RequestTimeout.INFINITE, null);
             if(ids != null && ids.size() > 0) {
                 robotId = ids.get(0);
             }
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        logger.debug(String.format("Robot %s has taken water - wait %d ms", robotId, WATER_INTERVAL_MS));

        try {
            Thread.sleep(WATER_INTERVAL_MS);
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }

        Water water = new Water();
        water.setAmount(250);

        try {
            logger.debug("trying to put new water");

            capi.delete(request.getContainer(), accessorSelector, MzsConstants.RequestTimeout.ZERO, null);
            capi.write(request.getContainer(), new Entry(water, LabelCoordinator.newCoordinationData(StorageServiceImpl.WATER_LABEL)));
            capi.write(request.getContainer(), new Entry("token", LabelCoordinator.newCoordinationData(StorageServiceImpl.WATER_TOKEN_LABEL)));
            logger.debug("put new water");
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
*/
        return AspectResult.OK;

    }


}
