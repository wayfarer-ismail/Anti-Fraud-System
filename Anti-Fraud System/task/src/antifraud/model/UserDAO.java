package antifraud.model;

import antifraud.model.response.UserResponse;
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
    private String role;
    private boolean accountNonLocked;

    // getters and setters

    public UserDAO(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = "Anonymous";
        this.accountNonLocked = false;
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

    public String getRole() {
        return role;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public UserResponse toUserResponse() {
        return new UserResponse(this.id, this.name, this.username, this.role);
    }
}