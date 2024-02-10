package system.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Logger;

public class SQLRunner {
    private static final Logger logger = Logger.getLogger(SQLRunner.class.getName());

    public static <T> T executeQuery(String sql, IResultSetIterator<T> rsIterator) {
        Optional<Connection> conn = Optional.empty();

        try {
            conn = ConnectionPool.getInstance().getConnection();

            try (Statement stmt = conn.orElseThrow().createStatement();
                    ResultSet rs = stmt.executeQuery(sql);) {
                return rsIterator.iterate(rs);
            } catch (SQLException e) {
                logger.severe("SQL State: %s\n%s".formatted(e.getSQLState(), e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe(e.getMessage());
            }
        } finally {
            ConnectionPool.getInstance().releaseConnection(conn.orElseThrow());
        }

        return null;
    }

    public static void execute(String sql) {
        Optional<Connection> conn = Optional.empty();

        try {
            conn = ConnectionPool.getInstance().getConnection();

            try (Statement stmt = conn.orElseThrow().createStatement();) {
                stmt.execute(sql);
            } catch (SQLException e) {
                logger.severe("SQL State: %s\n%s".formatted(e.getSQLState(), e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe(e.getMessage());
            }
        } finally {
            ConnectionPool.getInstance().releaseConnection(conn.orElseThrow());
        }
    }

}
