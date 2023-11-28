package antifraud.model;

public class TransactionRequest {
    private double amount;

    public TransactionRequest() {
        // Default constructor
    }

    public TransactionRequest(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
