package model.http;

import java.time.LocalDateTime;

import system.common.DateHelper;

public record StatementTransactionResponse(int value, String type, String description, LocalDateTime createdAt) {
    @Override
    public String toString() {
        return """
                {
                    "valor": %d,
                    "tipo": "%s",
                    "descricao": "%s",
                    "realizada_em": "%s"
                }
                """.formatted(value, type, description, DateHelper.format(createdAt));
    }

}
