package controller;

import java.util.ArrayList;
import java.util.List;

import model.db.User;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.database.SQLRunner;
import system.http.IResponseHandler;
import system.http.SimpleJsonParser;

public class UserController implements IResponseHandler {
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        var usersResult = SQLRunner.executeQuery("select * from users;", (rs) -> {
            List<User> users = new ArrayList<>();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getLong("limit"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
            }

            return users;
        });

        return HttpResponse.ok(SimpleJsonParser.toJsonArray(usersResult));
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
