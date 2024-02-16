package system.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLRunner {
    private static final Logger logger = Logger.getLogger(SQLRunner.class.getName());

    public static <T> T executeQuery(Connection conn, String sql, IResultSetIterator<T> rsIterator) {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {
            return rsIterator.iterate(rs);
        } catch (SQLException e) {
            logger.severe("SQL State: %s\n%s".formatted(e.getSQLState(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }

        return null;
    }

    public static void execute(String sql) {
        try(var conn = ConnectionPool.getInstance().getDataSource().getConnection()) {
            try (Statement stmt = conn.createStatement();) {
                stmt.execute(sql);
            } catch (SQLException e) {
                logger.severe("SQL State: %s\n%s".formatted(e.getSQLState(), e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }
    }
}
