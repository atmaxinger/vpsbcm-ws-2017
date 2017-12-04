package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import com.impossibl.postgres.api.jdbc.PGConnection;
import javafx.geometry.Pos;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionServiceImpl implements TransactionService {

    private Connection connection;

    public Transaction beginTransaction(long timeoutMillis) {
        try {
            connection = PostgresHelper.getNewConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TransactionImpl(connection);
    }
}
