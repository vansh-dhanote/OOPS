package model;

public class Student extends User {
    public Student() {
        super();
    }

    public Student(int id, String name, String role, String password) {
        super(id, name, role, password);
    }

    @Override
    // Method overriding: Student provides its own dashboard text.
    public String displayDashboard() {
        return "Student Dashboard";
    }
}
