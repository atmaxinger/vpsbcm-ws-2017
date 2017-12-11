package at.ac.tuwien.complang.vpsbcm.robnur.postgres.service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

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

    final static Logger logger = Logger.getLogger(ServiceUtil.class);

    private static TransactionService transactionService = new TransactionServiceImpl();

    private static Transaction newTransaction() {
        TransactionImpl transaction = (TransactionImpl) transactionService.beginTransaction(-1);

        try {
            if(transaction.getConnection().isClosed()){
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! WHY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! WHY");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transaction;
    }

    /**
     * Prepares the arrow for the json queries
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

    public static <T extends Serializable> boolean writeItem(T item, String table, Transaction transaction) {
        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            ObjectMapper mapper = new ObjectMapper();

            statement.execute(String.format("INSERT INTO %s (DATA) VALUES ('%s')", table, mapper.writeValueAsString(item)));

            statement.close();
        } catch (SQLException | JsonProcessingException e) {
            System.err.println("writeItem - return false");
            e.printStackTrace();
            return false;
        }

        return true;
    }

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
            result = null;
            System.err.println("readAllItems - return null");
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> List<T> readAllItems(String table, Class<T> resultClass) {
        Transaction t = transactionService.beginTransaction(-1,"READ ALL ITEMS " + table);
        List<T> l = readAllItems(table, resultClass, t);
        t.commit();

        return l;
    }

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
            System.err.println("getAllItems - return null");
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> List<T> getAllItems(String table, Class<T> resultClass) {
        Transaction t = transactionService.beginTransaction(-1,"READ ALL ITEMS " + table);
        List<T> l = getAllItems(table, resultClass, t);
        t.commit();

        return l;
    }

    public static <T extends Serializable> T readItemByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {

        T result = null;

        try {

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();

            String query = String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName, parameterValue);
            System.out.println("QUERY: " + query);
            ResultSet rs = statement.executeQuery(query);

            if(rs.next()) {
                String data = rs.getString("data");
                result = mapper.readValue(data, resultClass);
            } else {
                System.out.println("No results for query " + query);
            }

            statement.close();

        } catch (SQLException | IOException e) {
            System.err.println("readItemByParameter - Ignoring (result = " + result + ")");
            e.printStackTrace();
        }

        return result;

    }


    public static <T extends Serializable> T getItemByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {

        T result = null;


        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            System.err.println("getItemByParameter - createStatement - returning null");
            e.printStackTrace();
            return null;
        }

        ResultSet rs = null;
        try {
            rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName, parameterValue));

            rs.next();
            String data = rs.getString("data");
            result = mapper.readValue(data, resultClass);

            deleteItemByParameter(parameterName, parameterValue, table, transaction);
        } catch (SQLException | IOException e) {
            System.err.println("getItemByParameter - select and delete - returning null");
            result = null;
            e.printStackTrace();
        }

        try {
            statement.close();
        } catch (SQLException e) {
            System.err.println("getItemByParameter - statement.close - Ignoring");
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> List<T> getItemsById(String id, String table, Class<T> resultClass, Transaction transaction) {
        return getItemsByParameter("'id'", id, table, resultClass, transaction);
    }

    public static <T extends Serializable> List<T> getItemsByParameter(String parameterName, String parameterValue, String table, Class<T> resultClass, Transaction transaction) {
        logger.debug(String.format("getItemsByParameter(\"%s\",\"%s\",\"%s\",%s,%s)", parameterName, parameterValue, table, resultClass, transaction));

        List<T> result = new ArrayList<>();


        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Statement statement = null;
        try {
            statement = ((TransactionImpl) transaction).getConnection().createStatement();
        } catch (SQLException e) {
            System.err.println("getItemsByParameter - create statement - returning null");
            e.printStackTrace();
            return null;
        }

        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName, parameterValue);
            logger.debug("QUERY: " + query);
            rs = statement.executeQuery(query);


            while (rs.next()) {
                String data = rs.getString("data");
                result.add(mapper.readValue(data, resultClass));

                deleteItemByParameter(parameterName, parameterValue, table, transaction);
            }
        } catch (SQLException | IOException e) {
            result = null;
            System.err.println("getItemsByParameter - select and delete - returning null");
            e.printStackTrace();
        }

        try {
            statement.close();
        } catch (SQLException e) {
            System.err.println("getItemsByParameter - statement close - Ignoring");
            e.printStackTrace();
        }

        return result;
    }

    public static <T extends Serializable> T getItemById(String id, String table, Class<T> resultClass, Transaction transaction) {
        return getItemByParameter("'id'", id, table, resultClass, transaction);
    }

    public static void deleteItemByParameter(String parameterName, String parameterValue, String table, Transaction transaction) throws SQLException {

        logger.debug(String.format("deleteItemByParameter(\"%s\",\"%s\",\"%s\",%s", parameterName, parameterValue, table, transaction));


        try {
            Statement statement = ((TransactionImpl) transaction).getConnection().createStatement();
            statement.execute(String.format("DELETE FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName, parameterValue));
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info(String.format("DELETE FROM %s WHERE (data " + prepareArrow(parameterName) + " %s)::text = '%s'", table, parameterName, parameterValue));
            throw e;
        }

    }

    public static void deleteItemById(String id, String table, Transaction transaction) throws SQLException {
        deleteItemByParameter("'id'", id, table, transaction);
    }

    public static void deleteItemById(String id, String table) throws SQLException {
        Transaction t = newTransaction();
        deleteItemById(id, table, t);
        t.commit();
    }

    public static String getTableName(String channel, String payload) {
        return channel.substring(0, channel.indexOf("_"));
    }

    public static DBOPERATION getOperation(String channel, String payload) {
        if (payload.equals("INSERT")) {
            return DBOPERATION.INSERT;
        } else if (payload.equals("DELETE")) {
            return DBOPERATION.DELETE;
        }


        return DBOPERATION.UNKNOWN;
    }
}