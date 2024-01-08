package antifraud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stolen_card")
public class StolenCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true)
    private String number;

    public StolenCard() {
    }

    public StolenCard(String number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

}
