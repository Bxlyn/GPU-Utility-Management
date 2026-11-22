import java.io.Serializable;

abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String name;
    

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    //Temporary, to be replaced with proper authentication
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}