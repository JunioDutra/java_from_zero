package system.database;

import static system.common.Env.CONCURRENCY_SIZE;
import static system.common.Env.DATABASE_PASSWORD;
import static system.common.Env.DATABASE_URL;
import static system.common.Env.DATABASE_USER;
import static system.common.Env.getEnvInt;
import static system.common.Env.getEnvStr;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {
    private static ConnectionPool instance;

    private HikariDataSource ds;

    private ConnectionPool() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getEnvStr(DATABASE_URL, "jdbc:postgresql://localhost:5432/postgres"));
        config.setUsername(getEnvStr(DATABASE_USER, "postgres"));
        config.setPassword(getEnvStr(DATABASE_PASSWORD, "postgres"));
        config.setMaximumPoolSize(getEnvInt(CONCURRENCY_SIZE, 10));
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
