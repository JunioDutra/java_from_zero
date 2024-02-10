package model.http;

public record TransactionResponse(long limit, long balance) {
    @Override
    public String toString() {
        return """
                {
                    "limite": %d,
                    "saldo": %d
                }
                """.formatted(limit, balance);
    }
}
