package antifraud.model.request;

public record TransactionRequest(Long amount, String ip, String number) {
}
