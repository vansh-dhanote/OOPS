package model;

public interface PortalActions {
    boolean login(String password);
    void logout();
    String displayDashboard();
}
