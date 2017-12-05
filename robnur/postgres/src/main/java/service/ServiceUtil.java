package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtil {

    private static Transaction newTransaction() {
        return (new TransactionServiceImpl()).beginTransaction(-1);
    }

    public static <T extends Serializable> void writeItem(T item, String table, Transaction transaction) {
        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ObjectMapper mapper = new ObjectMapper();

            statement.execute(String.format("INSERT INTO %s (DATA) VALUES ('%s')", table, mapper.writeValueAsString(item)));

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> void writeItem(T item, String table) {
        Transaction t = newTransaction();
        writeItem(item, table, null);
        t.commit();
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
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, @NotNull Transaction transaction) {

        T result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE data ->> %s = '%s'", table,parameterName,parameterValue));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, resultClass);

            deleteItemByParameter(parameterName,parameterValue,table,transaction);

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> List<T> getItemsById(String id, String table, Class<T> resultClass, @NotNull Transaction transaction) {
        return getItemsByParameter("'id'",id,table,resultClass,transaction);
    }

    public static <T extends Serializable> List<T> getItemsByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, @NotNull Transaction transaction) {

        List<T> result = new ArrayList<>();

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE data ->> %s = '%s'", table,parameterName,parameterValue));

            while (rs.next()) {
                String data = rs.getString("data");
                result.add(mapper.readValue(data, resultClass));

                deleteItemByParameter(parameterName, parameterValue, table, transaction);
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> T getItemById(String id, String table, Class<T> resultClass, Transaction transaction) {
        return getItemByParameter("'id'",id,table,resultClass,transaction);
    }

    public static void deleteItemByParameter(String parameterName, String parameterValue, String table, @NotNull Transaction transaction) {
        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            statement.execute(String.format("DELETE FROM %s WHERE data ->> %s = '%s'", table, parameterName,parameterValue));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItemById(String id, String table, Transaction transaction) {
        deleteItemByParameter("'id'",id,table,transaction);
    }

    public static void deleteItemById(String id, String table) {
        deleteItemById(id,table, null);
    }
}