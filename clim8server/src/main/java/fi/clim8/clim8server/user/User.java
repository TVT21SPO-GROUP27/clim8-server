package fi.clim8.clim8server.user;

import java.io.Serializable;

public class User implements Serializable  {

    private static final long serialVersionUID = 5926468583005150707L;
    
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
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
