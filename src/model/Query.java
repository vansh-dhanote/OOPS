package model;

import java.sql.Timestamp;

public class Query {
    private int id;
    private int studentId;
    private int facultyId;
    private String studentName;
    private String facultyName;
    private String subject;
    private String question;
    private String answer;
    private String status;
    private String filePath;
    private String priority;
    private Timestamp submittedAt;

    public Query() {
    }

    public Query(int studentId, String subject, String question) {
        this.studentId = studentId;
        this.subject = subject;
        this.question = question;
        this.status = "Pending";
    }

    public Query(int studentId, int facultyId, String subject, String question, String filePath, String priority) {
        this(studentId, subject, question);
        this.facultyId = facultyId;
        this.filePath = filePath;
        this.priority = priority;
        this.answer = "No reply yet";
    }

    public Query(int id, int studentId, int facultyId, String studentName, String facultyName, String subject,
                 String question, String answer, String status, String filePath, String priority, Timestamp submittedAt) {
        this.id = id;
        this.studentId = studentId;
        this.facultyId = facultyId;
        this.studentName = studentName;
        this.facultyName = facultyName;
        this.subject = subject;
        this.question = question;
        this.answer = answer;
        this.status = status;
        this.filePath = filePath;
        this.priority = priority;
        this.submittedAt = submittedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "Query #" + id + " | " + subject + " | " + priority + " | Status: " + status;
    }
}
