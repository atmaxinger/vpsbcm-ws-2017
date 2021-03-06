package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtil {

    final static Logger logger = Logger.getLogger(ServiceUtil.class);

    private static TransactionService transactionService = new TransactionServiceImpl();

    private static Transaction newTransaction() {
        TransactionImpl transaction = (TransactionImpl) transactionService.beginTransaction(-1);

        return transaction;
    }

    /**
     * prepares the arrow for the json queries
     * This is needed because "->" gives you a json object and "->>" gives you the content of a json object.
     * If the subquery includes a "->" we also need to give a "->".
     *
     * @param d
     * @return
     */
    private static String prepareArrow(String d) {
        if (d.contains("->")) {
            return "->";
        } else return "->>";
    }

    /**
     * writes an item into a table of a foreign data base
     * @param item the item to write
     * @param address the address of the data base
     * @param table the name of the table
     * @param <T> type
     * @return returns true if successful, false otherwise
     */
    public static <T extends Serializable> boolean writeItemIntoForeignDb(T item, String address, String table) {
        try {
            Connection connection = PostgresHelper.getConnectionForUrl(address);
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();

            ObjectMapper mapper = new ObjectMapper();

            statement.execute(String.format("INSERT INTO %s (DATA) VALUES ('%s')", table, mapper.writeValueAsString(item)));

            statement.close();
            connection.close();
        } catch (SQLException | JsonProcessingException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }

        return true;
    }

    /**
     * write an item to the database
     * @param item the item to write
     * @param table the name of the table
     * @param transaction the transaction
     * @param <T> type
     * @return true if write was successful, false otherwise
     */
    public static <T extends Serializable> boolean writeItem(T item, String table, Transaction transaction) {
        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ObjectMapper mapper = new ObjectMapper();

            statement.execute(String.format("INSERT INTO %s (DATA) VALUES ('%s')", table, mapper.writeValueAsString(item)));

            statement.close();
        } catch (SQLException | JsonProcessingException e) {
            logger.trace("EXCEPTION", e);
            return false;
        }

        return true;
    }

    /**
     * write an item to the database
     * @param item the item to write
     * @param table the name of the table
     * @param <T> type
     * @return true if write was successful, false otherwise
     */
    public static <T extends Serializable> boolean writeItem(T item, String table) {
        boolean res = false;
        Transaction transaction = newTransaction();
        res = writeItem(item, table, transaction);
        if(!res) {
            transaction.rollback();
        } else {
            transaction.commit();
        }
        return res;
    }

    /**
     * reads all items
     * @param table the name of the table
     * @param typeReference the typeReference to convert the json into a object
     * @param transaction the transaction
     * @param <T> type
     * @return list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> List<T> readAllItems(String table, TypeReference typeReference, Transaction transaction) {

        List<T> result = new ArrayList<T>();

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + table);

            while (rs.next()) {
                String data = rs.getString("data");
                T t = mapper.readValue(data, typeReference);
                result.add(t);
            }

            statement.close();
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        return result;
    }


    /**
     * read all items from table
     * @param table the name of the table
     * @param resultClass the expected class of the result
     * @param transaction the transaction
     * @param <T> type
     * @return list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> List<T> readAllItems(String table, Class<T> resultClass, Transaction transaction) {
        return readAllItems(table, new TypeReference<T>() {
            @Override
            public Type getType() {
                return resultClass;
            }
        }, transaction);
    }

    /**
     * read all items from table
     * @param table the name of the table
     * @param resultClass the expected class of the result
     * @param <T> type
     * @return list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> List<T> readAllItems(String table, Class<T> resultClass) {
        Transaction t = transactionService.beginTransaction(-1,"READ ALL ITEMS " + table);
        List<T> l = readAllItems(table, resultClass, t);
        t.commit();

        return l;
    }

    /**
     * read all items from table
     * @param table the name of the table
     * @param typeReference the typeReference to convert the json into a object
     * @param <T> type
     * @return list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> List<T> readAllItems(String table, TypeReference typeReference) {
        Transaction t = transactionService.beginTransaction(-1,"READ ALL ITEMS " + table);
        List<T> l = readAllItems(table, typeReference, t);
        t.commit();
        return l;
    }

    /**
     * get all items of a table (read + delete)
     * @param table the name of the table
     * @param resultClass the expected class of the result
     * @param transaction the transaction
     * @param <T> type
     * @return list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> List<T> getAllItems(String table, Class<T> resultClass, Transaction transaction) {
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

            statement.execute("DELETE FROM " + table);

            statement.close();
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    /**
     * get one item by parameter of the JSON (read + delete)
     * @param parameterName the name of the JSON parameter
     * @param parameterValue the value of the JSON parameter
     * @param table the name of the table
     * @param typeReference the typeReference to convert the json into a object
     * @param transaction the transaction
     * @param <T> type
     * @return the item if successful, null if unsuccessful
     */
    public static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, String table, TypeReference typeReference, Transaction transaction) {
        logger.debug("getItemByParameter()");

        T result = null;

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return null;
        }

        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s' LIMIT 1", table, parameterName, parameterValue);
            logger.debug("getItemByParameter - " + query);
            rs = statement.executeQuery(query);

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, typeReference);
            long databaseId = rs.getLong("id");

            deleteItemByDatabaseId(databaseId, table, transaction);
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        logger.debug("getItemByParameter() - fin");


        return result;
    }

    /**
     * get one item by parameter of the JSON (read + delete)
     * @param parameterName the name of the JSON parameter
     * @param parameterValue the value of the JSON parameter
     * @param table the name of the table
     * @param resultClass the expected class of the result
     * @param transaction the transaction
     * @param <T> type
     * @return the item if successful, null if unsuccessful
     */
    public static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {

        return getItemByParameter(parameterName, parameterValue, table,  new TypeReference<T>() {
            @Override
            public Type getType() {
                return resultClass;
            }
        }, transaction);
    }

    /**
     * get one item arbitrary item (read + delete)
     * @param table the name of the table
     * @param typeReference the typeReference to convert the json into a object
     * @param transaction the transaction
     * @param <T> type
     * @return the item if successful, null if unsuccessful
     */
    public static <T extends Serializable> T getItem(String table, TypeReference typeReference, Transaction transaction) {

        T result = null;

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            return null;
        }

        ResultSet rs = null;
        try {
            rs = statement.executeQuery(String.format("SELECT * FROM %s LIMIT 1", table));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, typeReference);
            long databaseId = rs.getLong("id");

            deleteItemByDatabaseId(databaseId, table, transaction);
        } catch (SQLException | IOException e) {
            result = null;
            logger.trace("EXCEPTION", e);
        }

        try {
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
        }

        return result;
    }

    /**
     * get one item arbitrary item (read + delete)
     * @param table the name of the table
     * @param resultClass the expected class of the result
     * @param transaction the transaction
     * @param <T> type
     * @return the item if successful, null if unsuccessful
     */
    public static <T extends Serializable> T getItem(String table, Class<T> resultClass, Transaction transaction) {
        return getItem(table, new TypeReference<T>() {
            @Override
            public Type getType() {
                return resultClass;
            }
        }, transaction);
    }

    /**
     * get one items by the id of the JSON (read + delete)
     * @param id the id of the JSON
     * @param table the name of the table
     * @param resultClass the expected class of the result
     * @param transaction the transaction
     * @param <T> type
     * @return the list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> T getItemById(String id, String table, Class<T> resultClass, Transaction transaction) {
        return getItemByParameter("'id'", id, table, new TypeReference<T>() {
            @Override
            public Type getType() {
                return resultClass;
            }
        }, transaction);
    }

    /**
     * get one items by the id of the JSON (read + delete)
     * @param id the id of the JSON
     * @param table the name of the table
     * @param typeReference the typeReference to convert the json into a object
     * @param transaction the transaction
     * @param <T> type
     * @return the list of items if successful (may be of size 0), null if unsuccessful
     */
    public static <T extends Serializable> T getItemById(String id, String table, TypeReference typeReference, Transaction transaction) {
        return getItemByParameter("'id'", id, table, typeReference, transaction);
    }

    /**
     * delete items that match this json parameter
     * @param parameterName the json parameter
     * @param parameterValue the value of the json parameter
     * @param table the name of the table
     * @param transaction the transaction
     * @throws SQLException
     */
    public static void deleteItemByParameter(String parameterName, String parameterValue, String table, Transaction transaction) throws SQLException {

        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            statement.execute(String.format("DELETE FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName, parameterValue));
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            throw e;
        }

    }

    /**
     * delete items that matches this json id
     * @param id the id
     * @param table the name of the table
     * @param transaction the transaction
     * @throws SQLException
     */
    public static void deleteItemById(String id, String table, Transaction transaction) throws SQLException {
        deleteItemByParameter("'id'", id, table, transaction);
    }

    /**
     * delete items that matches this json id
     * @param id the id
     * @param table the name of the table
     * @throws SQLException
     */
    public static void deleteItemById(String id, String table) throws SQLException {
        Transaction t = newTransaction();
        deleteItemById(id, table, t);
        t.commit();
    }

    // TODO: do we need this method?
    /**
     * delete items that matches this json id
     * @param id the id
     * @param table the name of the table
     * @param transaction the transaction
     * @throws SQLException
     */
    private static void deleteItemByDatabaseId(long id, String table, Transaction transaction) throws SQLException {
        logger.debug("deleteItemByDatabaseId()");

        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            String query = String.format("DELETE FROM %s WHERE id = %d", table, id);
            logger.debug("deleteItemByDatabaseId - " + query);
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            logger.trace("EXCEPTION", e);
            throw e;
        }

        logger.debug("deleteItemByDatabaseId - fin");
    }
}