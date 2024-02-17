package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Function;

import model.http.StatementBalanceResponse;
import model.http.StatementResponse;
import model.http.StatementTransactionListResponse;
import model.http.StatementTransactionResponse;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.database.IPrepareStatementIterator;
import system.database.IResultSetIterator;
import system.database.SQLRunner;
import system.exception.NotFoundException;
import system.http.IResponseHandler;

public class StatementController
        implements IResponseHandler, IResultSetIterator<HttpResponse>, Function<Throwable, HttpResponse> {
    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        var userId = httpRequest.pathVariable().orElseThrow();

        IPrepareStatementIterator ips = (ps) -> {
            ps.setInt(1, Integer.parseInt(userId));
        };

        return SQLRunner.executeQuery("""
                   SELECT t.*, u.limit, u.amount user_amount
                     FROM users u
                LEFT JOIN transactions t ON t.user_id = u.id
                    WHERE u.id = ?
                 ORDER BY created_at DESC LIMIT 10
                    """, ips, this, this);
    }

    @Override
    public HttpResponse handlePost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'handlePost'");
    }

    @Override
    public HttpResponse iterate(ResultSet rs) throws SQLException {
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
            throw new NotFoundException();
        }

        return HttpResponse.ok(new StatementResponse(
                new StatementBalanceResponse(userAmount, LocalDateTime.now(), limit),
                new StatementTransactionListResponse(transactions)).toString());
    }

    @Override
    public HttpResponse apply(Throwable t) {
        if (t instanceof NotFoundException) {
            return HttpResponse.notFound("User not found");
        }

        return HttpResponse.internalServerError("Internal server error");
    }
}
