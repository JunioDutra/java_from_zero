package model.db;

import java.util.Optional;

import system.database.SQLRunner;

public record Balance(int id, int userId, long amount) {
    public static Optional<Balance> findByUserId(int userId) {
        return SQLRunner.executeQuery("SELECT * FROM balance WHERE user_id = %s".formatted(userId), rs -> {
            if (rs.next()) {
                return Optional.of(
                        new Balance(
                                rs.getInt("id"),
                                rs.getInt("user_id"),
                                rs.getLong("amount")));
            }

            return Optional.empty();
        });
    }

    public static Balance updateAmount(Balance balance, char type, long value) {
        var amount = type == 'd' ? balance.amount() - value : balance.amount() + value;

        SQLRunner.execute("UPDATE balance SET amount = %s WHERE id = %s".formatted(amount, balance.id()));

        return new Balance(balance.id(), balance.userId(), amount);
    }
}
