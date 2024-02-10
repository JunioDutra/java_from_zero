package model.db;

import java.time.LocalDateTime;
import java.util.Optional;

import system.common.DateHelper;
import system.database.SQLRunner;

public record User(Integer id, String username, long limit, LocalDateTime createdAt) {
    @Override
    public final String toString() {
        return """
                {
                    "id": %d,
                    "username": "%s",
                    "limit": %d,
                    "created_at": "%s"
                }
                """.formatted(id, username, limit, DateHelper.format(createdAt));
    }

    public static Optional<User> findById(int id) {
        return SQLRunner.executeQuery("select * from users where id = %s".formatted(id), (rs) -> {
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getLong("limit"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }

            return Optional.empty();
        });
    }
}
