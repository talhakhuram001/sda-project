package SDA_FINAL_PROJECT;

public abstract class User {
    private String username;
    private String password;
    private String email;
    private String role; 

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
    public abstract void performRole();
    public abstract void viewProfile();
}
