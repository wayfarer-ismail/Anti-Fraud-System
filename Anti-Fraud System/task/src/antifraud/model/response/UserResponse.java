package antifraud.model.response;

public class UserResponse {
    private Long id;
    private String name;
    private String username;

    public UserResponse() {
        // Default constructor
    }

    public UserResponse(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
