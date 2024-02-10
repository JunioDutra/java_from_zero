package model.http;

import system.http.SimpleJsonParser;

public record TransactionRequest(long value, char type, String description) {

    public static TransactionRequest fromJson(String body) {
        var props = SimpleJsonParser.objectToMap(body);
        return new TransactionRequest(
            Long.parseLong(props.get("valor").toString()),
            props.get("tipo").toString().charAt(0),
            props.get("descricao").toString()
        );
    }
} 