package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceUtil {

    public static <T extends Serializable> T getItemById(String id, ContainerReference containerReference, Transaction transaction, Capi capi) {
        return getItemByParameter("id",id,containerReference,transaction,capi);
    }

    public static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, ContainerReference containerReference, Transaction transaction, Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        Query query = new Query();

        ComparableProperty idProperty = ComparableProperty.forName(parameterName);
        java.util.List<Selector> selectors = Arrays.asList(QueryCoordinator.newSelector(query.filter(idProperty.matches(parameterValue)).cnt(0,1), MzsConstants.Selecting.COUNT_MAX));

        List<T> result = null;

        try {
            result = capi.take(containerReference,selectors,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
            return result.get(0);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends Serializable> void writeItem(T item, ContainerReference containerReference, Transaction transaction, Capi capi){
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(new Entry(item),containerReference,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    public static void writeItem(Entry entry, ContainerReference containerReference, Transaction transaction, Capi capi){
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        try {
            capi.write(entry,containerReference,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> List<T> readAllItems(ContainerReference containerReference, Selector selector, Transaction transaction,Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<T> result = null;
        try {
            result = capi.read(containerReference, selector,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T extends Serializable> List<T> readAllItems(ContainerReference containerReference, Transaction transaction,Capi capi) {

        return readAllItems(containerReference, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_MAX), transaction,capi);
    }

    public static void deleteItemById(String id, ContainerReference containerReference, Transaction transaction, Capi capi) {
        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        Query query = new Query();

        ComparableProperty idProperty = ComparableProperty.forName("id");
        java.util.List<Selector> selectors = Arrays.asList(QueryCoordinator.newSelector(query.filter(idProperty.matches(id)).cnt(0,1), MzsConstants.Selecting.COUNT_MAX));


        try {
            capi.delete(containerReference,selectors,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> T readItem(Selector selector, ContainerReference containerReference, Transaction transaction, Capi capi) {

        TransactionReference transactionReference = TransactionServiceImpl.getTransactionReference(transaction);

        List<T> result = null;

        try {
            result = capi.read(containerReference,selector,MzsConstants.RequestTimeout.DEFAULT,transactionReference);
            return result.get(0);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return null;
    }
}
