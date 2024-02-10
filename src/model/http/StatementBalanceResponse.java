package model.http;

import java.time.LocalDateTime;

import system.common.DateHelper;

public record StatementBalanceResponse(long total, LocalDateTime statementDate, long limit) {
    @Override
    public String toString() {
        return """
                {
                    "total": %d,
                    "data_extrato": "%s",
                    "limite": %d
                }
                """.formatted(total, DateHelper.format(statementDate), limit);
    }
}
