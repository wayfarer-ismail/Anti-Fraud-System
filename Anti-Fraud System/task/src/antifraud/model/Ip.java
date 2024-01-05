package antifraud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ips")
public class Ip {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String ip;

    public Ip() {
    }

    public Ip(String ip) {
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }
}