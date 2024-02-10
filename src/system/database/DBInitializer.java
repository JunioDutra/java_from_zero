package system.database;

public class DBInitializer {
    public static void createSchema() {
        SQLRunner.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL4 PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    "limit" BIGINT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """);

        SQLRunner.execute("""
                CREATE TABLE IF NOT EXISTS balance (
                    id SERIAL4 PRIMARY KEY,
                    user_id INT4 NOT NULL,
                    amount BIGINT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                );
                """);

        SQLRunner.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id SERIAL4 PRIMARY KEY,
                    user_id INT4 NOT NULL,
                    amount INT8 NOT NULL,
                    type CHAR(1) NOT NULL,
                    description VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                );
                """);

        var exists = SQLRunner.executeQuery("""
                SELECT count(*) FROM users;
                """, resultSet -> {
            if (resultSet.next())
                return resultSet.getInt(1) > 0;
            return false;
        });

        if (!exists) {
            SQLRunner.execute("""
                    INSERT INTO users (username, "limit") VALUES ('user1', 100000);
                    INSERT INTO users (username, "limit") VALUES ('user2', 80000);
                    INSERT INTO users (username, "limit") VALUES ('user3', 1000000);
                    INSERT INTO users (username, "limit") VALUES ('user4', 10000000);
                    INSERT INTO users (username, "limit") VALUES ('user5', 500000);
                    """);

            SQLRunner.execute("""
                    INSERT INTO balance (user_id, amount) VALUES (1, 0);
                    INSERT INTO balance (user_id, amount) VALUES (2, 0);
                    INSERT INTO balance (user_id, amount) VALUES (3, 0);
                    INSERT INTO balance (user_id, amount) VALUES (4, 0);
                    INSERT INTO balance (user_id, amount) VALUES (5, 0);
                    """);
        }
    }
}
