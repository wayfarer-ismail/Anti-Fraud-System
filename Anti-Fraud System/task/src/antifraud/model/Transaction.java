package antifraud.model;

import antifraud.model.enums.Feedback;
import antifraud.model.enums.Region;
import antifraud.model.request.TransactionRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;
    private Long amount;
    private String ip;
    private String number;
    private Region region;
    private LocalDateTime date;
    private Feedback result = Feedback.NONE;
    private Feedback feedback = Feedback.NONE;

    public Transaction() {
    }

    public Transaction(Long amount, String ip, String number, Region region, LocalDateTime dateTime) {
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = dateTime;
    }

    public static Transaction fromTransactionRequest(TransactionRequest transaction) {
        return new Transaction(transaction.amount(), transaction.ip(), transaction.number(), Region.valueOf(transaction.region()), LocalDateTime.parse(transaction.date()));
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getIp() {
        return ip;
    }

    public String getNumber() {
        return number;
    }

    public Region getRegion() {
        return region;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Feedback getResult() {
        return result;
    }

    public String getFeedback() {
        return feedback.toString();
    }

    public void setResult(Feedback result) {
        this.result = result;
    }

    public void setFeedback(Feedback status) {
        this.feedback = status;
    }

    public boolean hasFeedback() {
        return feedback != Feedback.NONE;
    }
}
