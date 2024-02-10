package controller;

import model.db.Balance;
import model.db.Transaction;
import model.db.User;
import model.http.TransactionRequest;
import model.http.TransactionResponse;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.http.IResponseHandler;
import system.http.SimpleJsonParser;

public class TransactionController implements IResponseHandler {

    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'handleGet'");
    }

    @Override
    public HttpResponse handlePost(HttpRequest httpRequest) {
        var userId = httpRequest.pathVariable().orElseThrow();
        TransactionRequest request = TransactionRequest.fromJson(httpRequest.body());

        var optUser = User.findById(Integer.parseInt(userId));

        if (optUser.isEmpty()) {
            return HttpResponse.notFound(
                    SimpleJsonParser.simpleError("UserNotFound", "User not found"));
        }

        var user = optUser.get();
        var balance = Balance.findByUserId(user.id())
                .orElseThrow();

        if ('d' == request.type() && request.value() > user.limit() + balance.amount()) {
            return HttpResponse.unprocessableEntity(
                    SimpleJsonParser.simpleError("The value exceeds the limit",
                            "%s".formatted(user.limit())));
        }

        Transaction.save(new Transaction(
                0,
                user.id(),
                request.value(),
                request.type(),
                request.description(),
                null));

        balance = Balance.updateAmount(balance, request.type(), request.value());

        return HttpResponse.ok(new TransactionResponse(user.limit(), balance.amount()).toString());
    }
}
