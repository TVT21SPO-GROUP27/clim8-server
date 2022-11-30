package fi.clim8.clim8server.user;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;

    public User() {

    }

    public User(
            Long id,
            String username,
            String email,
            String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

}
