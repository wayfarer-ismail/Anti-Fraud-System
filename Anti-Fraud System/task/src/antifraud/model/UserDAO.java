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
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    // getters and setters

    public UserDAO(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = "Anonymous";
        this.accountNonExpired = true;
        this.accountNonLocked = false;
        this.credentialsNonExpired = true;
        this.enabled = true;
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

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public UserResponse toUserResponse() {
        return new UserResponse(this.id, this.name, this.username, this.role);
    }
}