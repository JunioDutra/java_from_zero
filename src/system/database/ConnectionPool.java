package system.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ConnectionPool {
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    private static ConnectionPool instance = null;
    private static String url = System.getenv("DATABASE_URL");
    private static String user = System.getenv("DATABASE_USER");
    private static String password = System.getenv("DATABASE_PASSWORD");
    private static int poolSize = 10;

    private List<Connection> connections;

    private ConnectionPool() {
        connections = new ArrayList<>();
        initializeConnections();
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void initializeConnections() {
        try {
            for (int i = 0; i < poolSize; i++) {
                Connection connection = DriverManager.getConnection(url, user, password);
                connections.add(connection);
            }
        } catch (SQLException e) {
            logger.severe("SQL State: %s\n%s".formatted(e.getSQLState(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }
    }

    public synchronized Optional<Connection> getConnection() {
        if (connections.isEmpty()) {
            logger.warning("Connection pool is empty");
            return Optional.empty();
        }

        Connection connection = connections.remove(connections.size() - 1);
        return Optional.of(connection);
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            connections.add(connection);
        }
    }
}
