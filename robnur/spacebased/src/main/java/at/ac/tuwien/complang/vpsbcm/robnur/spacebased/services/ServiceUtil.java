package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.apache.log4j.Logger;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceUtil {
    final static Logger logger = Logger.getLogger(ServiceUtil.class);

    public synchronized static <T extends Serializable> T getItemById(String id, ContainerReference containerReference, Transaction transaction, Capi capi) {
        return getItemByParameter("id",id,containerReference,transaction,capi);
    }

    public synchronized static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, ContainerReference containerReference, Transaction transaction, Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        Query query = new Query();

        ComparableProperty idProperty = ComparableProperty.forName(parameterName);
        java.util.List<Selector> selectors = Arrays.asList(QueryCoordinator.newSelector(query.filter(idProperty.matches(parameterValue)).cnt(0,1), MzsConstants.Selecting.COUNT_MAX));

        List<T> result = null;

        try {
            result = capi.take(containerReference,selectors,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
            if(result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        }
        catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        return null;
    }

    public synchronized static <T extends Serializable> void writeItem(T item, ContainerReference containerReference, Transaction transaction, Capi capi){
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(new Entry(item),containerReference,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public synchronized static void writeItem(Entry entry, ContainerReference containerReference, Transaction transaction, Capi capi){
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(entry,containerReference,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public synchronized static <T extends Serializable> List<T> readAllItems(ContainerReference containerReference, Selector selector, Transaction transaction,Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<T> result = null;
        try {
            result = capi.read(containerReference, selector,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
        return result;
    }

    public synchronized static <T extends Serializable> List<T> readAllItems(ContainerReference containerReference, Transaction transaction,Capi capi) {

        return readAllItems(containerReference, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), transaction,capi);
    }

    public synchronized static void deleteItemById(String id, ContainerReference containerReference, Transaction transaction, Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        Query query = new Query();

        ComparableProperty idProperty = ComparableProperty.forName("id");
        java.util.List<Selector> selectors = Arrays.asList(QueryCoordinator.newSelector(query.filter(idProperty.matches(id)).cnt(0,1), MzsConstants.Selecting.COUNT_MAX));


        try {
            capi.delete(containerReference,selectors,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
    }

    public synchronized static <T extends Serializable> T getItem(Selector selector, ContainerReference containerReference, Transaction transaction, Capi capi) {

        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<T> result = null;

        try {
            result = capi.take(containerReference,selector,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
            if(result != null && result.size() > 0) {
                return result.get(0);
            }
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }

        return null;
    }

    public synchronized static <T extends Serializable> List<T> getAllItems(ContainerReference containerReference, Selector selector, Transaction transaction,Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<T> result = null;
        try {
            result = capi.take(containerReference, selector,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsTimeoutException | TransactionException e) {
            TransactionServiceImpl.setTransactionTimedOut(transaction);
        } catch (MzsCoreException e) {
            logger.trace("EXCEPTION", e);
        }
        return result;
    }

    public static <T extends Serializable> List<T> getAllItems(ContainerReference containerReference, Transaction transaction, Capi capi) {
        return getAllItems(containerReference, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), transaction,capi);
    }
}
