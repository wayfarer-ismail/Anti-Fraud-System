package antifraud.model.request;

public class UserRequest {
    private String name;

    private String username;

    private String password;

    public UserRequest() {
        // Default constructor
    }

    public UserRequest(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // getters and setters

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
