package model;

public class Faculty extends User {
    private String subject;
    private String schedule;

    public Faculty() {
        super();
    }

    public Faculty(int id, String name, String role, String password) {
        super(id, name, role, password);
    }

    public Faculty(int id, String name, String role, String password, String subject, String schedule) {
        super(id, name, role, password);
        this.subject = subject;
        this.schedule = schedule;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    // Method overriding: Faculty provides its own dashboard text.
    public String displayDashboard() {
        return "Faculty Dashboard";
    }

    @Override
    public String toString() {
        return getName() + " (" + subject + ")";
    }
}
