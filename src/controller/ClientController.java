package controller;

import model.system.HttpRequest;
import model.system.HttpResponse;
import system.http.IResponsePathVarialbeHandler;
import system.http.SimpleJsonParser;

public class ClientController implements IResponsePathVarialbeHandler {
    private StatementController statementController = new StatementController();
    private TransactionController transactionController = new TransactionController();

    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        if (httpRequest.path().matches("/clientes/\\d+/extrato")) {
            return statementController.handleGet(httpRequest);
        }

        return HttpResponse.badRequest(
                SimpleJsonParser.simpleError("UnsuportedMethod", "Method not supported"));
    }

    @Override
    public HttpResponse handlePost(HttpRequest httpRequest) {
        if (httpRequest.path().matches("/clientes/\\d+/transacoes")) {
            return transactionController.handlePost(httpRequest);
        }

        return HttpResponse.badRequest(
                SimpleJsonParser.simpleError("UnsuportedMethod", "Method not supported"));
    }

    @Override
    public String regexPathVariable() {
        return "/clientes/(\\d+)/.*";
    }
}
