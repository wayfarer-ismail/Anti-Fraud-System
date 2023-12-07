package antifraud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    // getters and setters
    public UserDAO(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public UserDAO() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}