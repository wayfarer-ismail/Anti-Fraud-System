package antifraud.model;

import antifraud.model.request.TransactionRequest;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long amount;
    private String ip;
    private String number;

    public Transaction() {
    }

    public Transaction(Long amount, String ip, String number) {
        this.amount = amount;
        this.ip = ip;
        this.number = number;
    }

    public static Transaction fromTransactionRequest(TransactionRequest transaction) {
        return new Transaction(transaction.amount(), transaction.ip(), transaction.number());
    }

    public Long getId() {
        return id;
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
}
