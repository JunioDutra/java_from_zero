package system.database;

import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    private static ConnectionPool instance;

    private HikariDataSource ds;

    private ConnectionPool() {
        HikariConfig config = new HikariConfig();

        logger.info("DATABASE_URL: " + System.getenv("DATABASE_URL"));
        logger.info("DATABASE_USER: " + System.getenv("DATABASE_USER"));
        logger.info("DATABASE_PASSWORD: " + System.getenv("DATABASE_PASSWORD"));

        config.setJdbcUrl(System.getenv("DATABASE_URL"));
        config.setUsername(System.getenv("DATABASE_USER"));
        config.setPassword(System.getenv("DATABASE_PASSWORD"));
        config.setMaximumPoolSize(200);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.ds = new HikariDataSource(config);
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }

        return instance;
    }

    public HikariDataSource getDataSource() {
        return this.ds;
    }
}
