package antifraud.model.request;

public record TransactionRequest(double amount, String ip, String number) {
}
