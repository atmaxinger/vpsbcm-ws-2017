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

        Statement statement = null;

        setAutoCommit(transaction);

        try {
            statement = PostgresHelper.getConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            statement.execute(String.format("INSERT INTO %s (DATA) VALUES ('%s')", table, mapper.writeValueAsString(item)));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> void writeItem(T item, String table) {
        writeItem(item, table, null);
    }


    public static <T extends Serializable> List<T> readAllItems(String table, Class<T> resultClass, Transaction transaction) {
        List<T> result = new ArrayList<T>();

        setAutoCommit(transaction);

        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


            ResultSet rs = PostgresHelper.getConnection().createStatement().executeQuery("SELECT * FROM " + table);

            while (rs.next()) {
                String data = rs.getString("data");
                T t = mapper.readValue(data, resultClass);
                result.add(t);
            }
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
        return readAllItems(table, resultClass, null);
    }

    private static void setAutoCommit(Transaction transaction) {
        if (transaction == null) {
            try {
                PostgresHelper.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PostgresHelper.getConnection().setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T extends Serializable> T getItemById(String id, String table, Class<T> resultClass, Transaction transaction) {

        T result = null;

        setAutoCommit(transaction);

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            ResultSet rs = PostgresHelper.getConnection().createStatement().
                    executeQuery(String.format("SELECT * FROM %s WHERE data ->> 'id' = '%s'", table,id));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, resultClass);

            deleteItemById(id,table,transaction);

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

        setAutoCommit(transaction);

        try {
            PostgresHelper.getConnection().createStatement().
                    execute(String.format("DELETE FROM %s WHERE data ->> 'id' = '%s'", table, id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItemById(String id, String table) {
        deleteItemById(id,table,null);
    }
}