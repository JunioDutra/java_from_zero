package model.http;

public record StatementResponse(StatementBalanceResponse balance, StatementTransactionListResponse lastTransactions) {
    @Override
    public String toString() {
        return """
                {
                    "saldo": %s,
                    "ultimas_transacoes": %s
                }
                """.formatted(balance, lastTransactions);
    }
}
