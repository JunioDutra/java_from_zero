package model.http;

import system.http.SimpleJsonParser;

public record TransactionRequest(int value, char type, String description) {

    public static TransactionRequest fromJson(String body) {
        var props = SimpleJsonParser.objectToMap(body);
        var desc = props.get("descricao") == null ? "" : props.get("descricao").toString().trim();
        if (desc.isEmpty() || desc.isBlank() || desc.length() > 10) {
            throw new IllegalArgumentException("Description must be less than 10 characters");
        }

        return new TransactionRequest(
                Integer.parseInt(props.get("valor").toString()),
                props.get("tipo").toString().charAt(0),
                props.get("descricao").toString());
    }
}