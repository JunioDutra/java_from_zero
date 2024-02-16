package model.db;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import system.database.SQLRunner;

public record Transaction(int id, int userId, long amount, char type, String description, LocalDateTime createdAt) {
    public static void save(Transaction transaction) {
        var sql = """
                INSERT INTO transactions (user_id, amount, type, description)
                VALUES (%s, %s, '%s', '%s');
                """.formatted(
                transaction.userId(),
                transaction.amount(),
                transaction.type(),
                transaction.description());

        SQLRunner.execute(sql);
    }

    public static List<Transaction> list(Connection connection, Integer userId) {
        return SQLRunner.executeQuery(connection, "SELECT * FROM transactions WHERE user_id = %s ORDER BY created_at DESC LIMIT 10".formatted(userId), rs -> {
            var transactions = new ArrayList<Transaction>();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getLong("amount"),
                        rs.getString("type").charAt(0),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }

            return transactions;
        });
    }
}
