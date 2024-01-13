package antifraud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction_max_values")
public class TransactionMaxValues {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double maxAllow;
    private double maxManual;

    public TransactionMaxValues() {
    }

    public TransactionMaxValues(double maxAllow, double maxManual) {
        this.maxAllow = maxAllow;
        this.maxManual = maxManual;
    }

    public Long getId() {
        return id;
    }

    public double getMaxAllow() {
        return maxAllow;
    }

    public void setMaxAllow(double maxAllow) {
        this.maxAllow = maxAllow;
    }

    public double getMaxManual() {
        return maxManual;
    }

    public void setMaxManual(double maxManual) {
        this.maxManual = maxManual;
    }
}