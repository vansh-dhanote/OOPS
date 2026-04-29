package model;

public abstract class User implements PortalActions {
    // Encapsulation: data members are kept private and accessed through methods.
    private int id;
    private String name;
    private String role;
    private String password;
    private boolean loggedIn;

    public User() {
    }

    public User(int id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public boolean login(String password) {
        boolean valid = this.password != null && this.password.equals(password);
        setLoggedIn(valid);
        return valid;
    }

    @Override
    public void logout() {
        setLoggedIn(false);
    }

    @Override
    // Abstract method forces child classes to provide their own dashboard label.
    public abstract String displayDashboard();
}
