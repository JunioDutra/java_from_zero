package model.http;

import java.util.List;

import system.http.SimpleJsonParser;

public record StatementTransactionListResponse(List<StatementTransactionResponse> transactions) {
    @Override
    public String toString() {
        return SimpleJsonParser.toJsonArray(transactions);
    }

}
