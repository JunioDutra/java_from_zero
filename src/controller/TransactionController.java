package controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import model.db.User;
import model.http.TransactionRequest;
import model.http.TransactionResponse;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.database.ConnectionPool;
import system.exception.NotFoundException;
import system.http.IResponseHandler;
import system.http.SimpleJsonParser;

public class TransactionController implements IResponseHandler {
    private List<User> users = new ArrayList<User>();

    public TransactionController() {
        users = User.list();
    }

    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'handleGet'");
    }

    @Override
    public HttpResponse handlePost(HttpRequest httpRequest) {
        try (var con = ConnectionPool.getInstance().getDataSource().getConnection();
                var pst = con.prepareStatement("""
                           UPDATE users
                              SET amount = amount + ?
                            WHERE id =  ?
                        RETURNING amount;
                         """)) {
            con.setAutoCommit(false);

            TransactionRequest request = TransactionRequest.fromJson(httpRequest.body());
            var userId = httpRequest.pathVariable().orElseThrow();
            var user = User.find(users, Integer.parseInt(userId));

            if (!List.of('c', 'd').contains(request.type())) {
                return HttpResponse.unprocessableEntity(
                        SimpleJsonParser.simpleError("UnprocessableEntity", "Unprocessable entity"));
            }

            long amount = 0;
            pst.setInt(1, request.type() == 'd' ? request.value() * -1 : request.value());
            pst.setInt(2, Integer.parseInt(userId));

            try (var rs = pst.executeQuery()) {
                if (rs.next()) {
                    amount = rs.getLong("amount");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (amount < user.limit() * -1) {
                // if (request.type() == 'd')
                //     System.out.println("Rollback values: %s, %s".formatted(user.limit(), amount));
                con.rollback();
            } else {
                // if (request.type() == 'd')
                //     System.out.println("Valores Ok: %s, %s".formatted(user.limit(), amount));
                con.commit();
                insertTransaction(con, user, request);
            }

            con.setAutoCommit(true);

            return HttpResponse.ok(new TransactionResponse(user.limit(), amount).toString());
        } catch (NotFoundException e) {
            return HttpResponse.notFound(SimpleJsonParser.simpleError("NotFound", "Not found"));
        } catch (IllegalArgumentException e) {
            return HttpResponse.unprocessableEntity("Unprocessable entity");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.unprocessableEntity("Unprocessable entity");
        }
    }

    private void insertTransaction(Connection con, User user, TransactionRequest request) {
        try(
            var pst = con.prepareStatement("""
                INSERT INTO transactions (user_id, amount, type, description)
                                  VALUES (?, ?, ?, ?);
                 """)) {
            pst.setInt(1, user.id());
            pst.setInt(2, request.value());
            pst.setString(3, String.valueOf(request.type()));
            pst.setString(4, request.description());

            pst.execute();
            con.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
