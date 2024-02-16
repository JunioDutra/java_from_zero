package system.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import system.common.Env;

public class ConnectionPool {
    private static final int CONCURRENCY_SIZE = Env.getEnvInt("CONCURRENCY_SIZE", 10);
    private static final String DATABASE_URL = Env.getEnvStr("DATABASE_URL",
            "jdbc:postgresql://localhost:5432/postgres");
    private static final String DATABASE_USER = Env.getEnvStr("DATABASE_USER", "postgres");
    private static final String DATABASE_PASSWORD = Env.getEnvStr("DATABASE_PASSWORD", "postgres");

    private static ConnectionPool instance;

    private HikariDataSource ds;

    private ConnectionPool() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(DATABASE_URL);
        config.setUsername(DATABASE_USER);
        config.setPassword(DATABASE_PASSWORD);
        config.setMaximumPoolSize(CONCURRENCY_SIZE);
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
