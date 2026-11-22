package backend;

public class Admin extends User {
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "gpu2026";

    public Admin() {
        super(ADMIN_USER, ADMIN_PASS, "Admin");
    }

    
}