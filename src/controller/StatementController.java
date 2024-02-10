package controller;

import java.time.LocalDateTime;

import model.db.Balance;
import model.db.Transaction;
import model.db.User;
import model.http.StatementBalanceResponse;
import model.http.StatementResponse;
import model.http.StatementTransactionListResponse;
import model.http.StatementTransactionResponse;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.http.IResponseHandler;
import system.http.SimpleJsonParser;

public class StatementController implements IResponseHandler {
    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        var userId = httpRequest.pathVariable().orElseThrow();
        var optUser = User.findById(Integer.parseInt(userId));

        if (optUser.isEmpty()) {
            return HttpResponse.notFound(
                    SimpleJsonParser.simpleError("UserNotFound", "User not found"));
        }

        var user = optUser.get();
        var balance = Balance.findByUserId(user.id())
                .orElseThrow();

        var transactions = Transaction.list(user.id());

        var response = new StatementResponse(
                new StatementBalanceResponse(balance.amount(), LocalDateTime.now(), user.limit()),
                new StatementTransactionListResponse(transactions.stream()
                        .map(t -> new StatementTransactionResponse(
                                t.amount(),
                                t.type(),
                                t.description(),
                                t.createdAt()))
                        .toList()));

        return HttpResponse.ok(response.toString());
    }

    @Override
    public HttpResponse handlePost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'handlePost'");
    }
}
