package model.db;

import java.util.ArrayList;
import java.util.List;

import system.database.ConnectionPool;
import system.exception.NotFoundException;

public record User(int id, int limit, int amount) {
    public static List<User> list() {
        try (var con = ConnectionPool.getInstance().getDataSource().getConnection();
                var pst = con.prepareStatement("SELECT * FROM users");
                var rs = pst.executeQuery()) {
            var users = new ArrayList<User>();

            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getInt("limit"), rs.getInt("amount")));
            }

            return users;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static User find(List<User> users, int id) {
        return users.stream().filter(u -> u.id() == id).findFirst().orElseThrow(()-> new NotFoundException());
    }
}
