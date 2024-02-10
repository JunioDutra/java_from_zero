package system.database;

import static system.common.Env.CONECTION_SIZE;
import static system.common.Env.DATABASE_PASSWORD;
import static system.common.Env.DATABASE_URL;
import static system.common.Env.DATABASE_USER;
import static system.common.Env.getEnvInt;
import static system.common.Env.getEnvStr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ConnectionPool {
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    private static final int MAX_CONNECTIONS = getEnvInt(CONECTION_SIZE, 10);

    private static ConnectionPool instance;

    private BlockingQueue<Connection> connections = new LinkedBlockingQueue<>(MAX_CONNECTIONS);

    private ConnectionPool() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
            var suppiers = new ArrayList<CompletableFuture<Connection>>();
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                suppiers.add(CompletableFuture.supplyAsync(() -> {
                    while (true) {
                        try {
                            var connection = DriverManager.getConnection(
                                    getEnvStr(DATABASE_URL, "jdbc:postgresql://localhost:5432/postgres"),
                                    getEnvStr(DATABASE_USER, "postgres"),
                                    getEnvStr(DATABASE_PASSWORD, "postgres"));

                            if (connection != null && !connection.isClosed() && connection.isValid(100)) {
                                logger.info("Connection created");
                                return connection;
                            }
                        } catch (SQLException e) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                logger.warning("Failed to sleep");
                            }
                        }
                    }
                }));
            }

            suppiers.forEach(task -> {
                try {
                    var cc = task.get();
                    if (cc != null) {
                        instance.connections.put(cc);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.warning("Failed to create connection");
                }
            });

            logger.info("Connection pool created with %s connections".formatted(instance.connections.size()));
        }

        return instance;
    }

    public synchronized Connection getConnection() {
        var connection = connections.poll();
        do {
            try {
                var conIsCreated = connection != null;
                var conIsValid = conIsCreated && connection.isValid(100);
                var conIsOpen = conIsValid && !connection.isClosed();

                if (conIsOpen) {
                    break;
                }

                logger.warning(
                        "Invalid connection, trying to get another one, current size: %s, is valid: %s, is open: %s, is created: %s"
                                .formatted(connections.size(), conIsValid, conIsOpen, conIsCreated));

                try {
                    createSilentConnection();
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    logger.warning("Failed to sleep");
                }

                connection = connections.poll();
            } catch (Exception e) {
                e.printStackTrace();
                logger.warning("Failed to validate connection");
            }
        } while (connection == null);

        // logger.info("Connection getted, current size: %s".formatted(connections.size()));

        return new CustomConnection(connection);
    }

    public synchronized void releaseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(100)) {
                connections.put(connection);
            }
        } catch (InterruptedException | SQLException e) {
            logger.warning("Failed to release connection");
        }
    }

    private void createSilentConnection() {
        CompletableFuture.runAsync(() -> {
            try {
                var connection = DriverManager.getConnection(
                        getEnvStr(DATABASE_URL, "jdbc:postgresql://localhost:5432/postgres"),
                        getEnvStr(DATABASE_USER, "postgres"),
                        getEnvStr(DATABASE_PASSWORD, "postgres"));

                if (connection != null && !connection.isClosed() && connection.isValid(100)) {
                    connections.put(connection);
                }
            } catch (Exception e) {
                logger.warning("Failed to create connection");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    logger.warning("Failed to sleep");
                }
            }
        });
    }
}
