package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
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
        writeItem(item, table, new TransactionImpl(PostgresHelper.getConnection()));
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
        return readAllItems(table, resultClass, new TransactionImpl(PostgresHelper.getConnection()));
    }

    public static <T extends Serializable> T getItemById(String id, String table, Class<T> resultClass, Transaction transaction) {

        T result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE data ->> 'id' = '%s'", table,id));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, resultClass);

            deleteItemById(id,table,transaction);

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

    public static void deleteItemById(String id, String table, Transaction transaction) {

        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            statement.execute(String.format("DELETE FROM %s WHERE data ->> 'id' = '%s'", table, id));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItemById(String id, String table) {
        deleteItemById(id,table, new TransactionImpl(PostgresHelper.getConnection()));
    }
}