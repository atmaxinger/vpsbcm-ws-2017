package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtil {

    public enum DBOPERATION {
        UNKNOWN,
        INSERT,
        DELETE
    }

    private static TransactionService transactionService = new TransactionServiceImpl();

    private static Transaction newTransaction() {
        return transactionService.beginTransaction(-1);
    }

    /**
     * Prepares the arrow for the json queries
     * This is needed because "->" gives you a json object and "->>" gives you the content of a json object.
     * If the subquery includes a "->" we also need to give a "->".
     * @param d
     * @return
     */
    private static String prepareArrow(String d) {
        if(d.contains("->")) {
            return "->";
        }

        else return "->>";
    }

    public static <T extends Serializable> void writeItem(T item, String table, Transaction transaction) {
        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ObjectMapper mapper = new ObjectMapper();

            statement.execute(String.format("INSERT INTO %s (DATA) VALUES ('%s')", table, mapper.writeValueAsString(item)));

            statement.close();
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> void writeItem(T item, String table) {
        Transaction transaction = newTransaction();
        writeItem(item, table, transaction);
        transaction.commit();
    }


    public static <T extends Serializable> List<T> readAllItems(String table, Class<T> resultClass, Transaction transaction) {
        List<T> result = new ArrayList<T>();

        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + table);

            while (rs.next()) {
                String data = rs.getString("data");
                T t = mapper.readValue(data, resultClass);
                result.add(t);
            }

            statement.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> List<T> readAllItems(String table, Class<T> resultClass) {
        Transaction t = newTransaction();
        List<T> l = readAllItems(table, resultClass,t);
        t.commit();

        return l;
    }

    public static <T extends Serializable> T readItemByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {

        T result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table,parameterName,parameterValue));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, resultClass);

            statement.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return result;

    }


    public static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {

        T result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table,parameterName,parameterValue));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, resultClass);

            deleteItemByParameter(parameterName,parameterValue,table,transaction);

            statement.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> List<T> getItemsById(String id, String table, Class<T> resultClass, Transaction transaction) {
        return getItemsByParameter("'id'",id,table,resultClass,transaction);
    }

    public static <T extends Serializable> List<T> getItemsByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {

        List<T> result = new ArrayList<>();

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table,parameterName,parameterValue));

            while (rs.next()) {
                String data = rs.getString("data");
                result.add(mapper.readValue(data, resultClass));

                deleteItemByParameter(parameterName, parameterValue, table, transaction);
            }

            statement.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> T getItemById(String id, String table, Class<T> resultClass, Transaction transaction) {
        return getItemByParameter("'id'",id,table,resultClass,transaction);
    }

    public static void deleteItemByParameter(String parameterName, String parameterValue, String table, Transaction transaction) {
        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            statement.execute(String.format("DELETE FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName,parameterValue));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItemById(String id, String table, Transaction transaction) {
        deleteItemByParameter("'id'",id,table,transaction);
    }

    public static void deleteItemById(String id, String table) {
        Transaction t = newTransaction();
        deleteItemById(id,table, t);
        t.commit();
    }

    public static String getTableName(String channel, String payload) {
        return channel.substring(0,channel.indexOf("_"));
    }

    public static DBOPERATION getOperation(String channel, String payload) {
        if(payload.equals("INSERT")) {
            return DBOPERATION.INSERT;
        }
        else if (payload.equals("DELETE")) {
            return DBOPERATION.DELETE;
        }


        return DBOPERATION.UNKNOWN;
    }
}