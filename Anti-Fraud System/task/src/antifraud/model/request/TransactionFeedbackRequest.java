package antifraud.model.request;

public record TransactionFeedbackRequest(long transactionId, String feedback) {
}
