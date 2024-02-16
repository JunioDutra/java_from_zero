package controller;

import java.time.LocalDateTime;
import java.util.ArrayList;

import model.http.StatementBalanceResponse;
import model.http.StatementResponse;
import model.http.StatementTransactionListResponse;
import model.http.StatementTransactionResponse;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.database.ConnectionPool;
import system.http.IResponseHandler;

public class StatementController implements IResponseHandler {
    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        var userId = httpRequest.pathVariable().orElseThrow();

        StatementResponse response = null;

        try (var connection = ConnectionPool.getInstance().getDataSource().getConnection();
                var statement = connection.prepareStatement("""
                        select t.*, u.limit, u.amount user_amount 
                        from users u 
                        left join transactions t on t.user_id = u.id 
                        where u.id = ?
                        order by created_at desc limit 10
                            """)) {

            statement.setInt(1, Integer.parseInt(userId));

            try (var rs = statement.executeQuery()) {
                var transactions = new ArrayList<StatementTransactionResponse>();

                var limit = -1;
                var userAmount = -1;

                while (rs.next()) {
                    limit = rs.getInt("limit");
                    userAmount = rs.getInt("user_amount");
                    
                    rs.getInt("amount");
                    if (rs.wasNull()) {
                        break;
                    }

                    transactions.add(new StatementTransactionResponse(
                            rs.getInt("amount"),
                            rs.getString("type"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at").toLocalDateTime()));

                }

                if (limit == -1 || userAmount == -1) {
                    return HttpResponse.notFound("User not found");
                }

                response = new StatementResponse(
                        new StatementBalanceResponse(userAmount, LocalDateTime.now(), limit),
                        new StatementTransactionListResponse(transactions));
            }
        } catch (Exception e) {
            return HttpResponse.internalServerError(e.getMessage());
        }

        return HttpResponse.ok(response.toString());
    }

    @Override
    public HttpResponse handlePost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'handlePost'");
    }
}
