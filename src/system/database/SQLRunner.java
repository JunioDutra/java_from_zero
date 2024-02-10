package system.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Function;
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

    @SuppressWarnings("unchecked")
    public static <RESPONSE, ERROR extends Throwable> RESPONSE executeQuery(String sql, IPrepareStatementIterator istmt,
            IResultSetIterator<RESPONSE> irs,
            Function<? super ERROR, RESPONSE> exceptionHandler) throws ERROR {
        try (var connection = ConnectionPool.getInstance().getConnection();
                var statement = connection.prepareStatement(sql)) {

            istmt.iterate(statement);

            try (var rs = statement.executeQuery()) {
                return irs.iterate(rs);
            }
        } catch (Throwable e) {
            return exceptionHandler.apply((ERROR) e);
        }
    }

    public static void execute(String sql) {
        try (var conn = ConnectionPool.getInstance().getConnection();
                Statement stmt = conn.createStatement();) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.severe("SQL State: %s\n%s".formatted(e.getSQLState(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }
    }
}
