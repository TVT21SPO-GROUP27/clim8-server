package fi.clim8.clim8server.user;


public class User {
    
    private Long id;
    private String name;
    private String userName;
    private String email;
    private String password;


    public User(){

    }

    public User(
            Long id,
            String name,
            String userName,
            String email,
            String password
            ) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.password = password;
      }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return userName;
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
