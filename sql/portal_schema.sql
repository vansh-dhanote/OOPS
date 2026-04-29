CREATE DATABASE IF NOT EXISTS faculty_student_portal;
USE faculty_student_portal;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    subject VARCHAR(100),
    schedule VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS queries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    faculty_id INT,
    subject VARCHAR(100) NOT NULL,
    question TEXT NOT NULL,
    answer TEXT,
    file_path VARCHAR(255),
    priority VARCHAR(20),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) DEFAULT 'Pending',
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (faculty_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    message TEXT NOT NULL,
    posted_by VARCHAR(100) NOT NULL
);

INSERT INTO users (id, name, role, password, subject, schedule) VALUES
(101, 'Aarav Student', 'student', 'student123', NULL, NULL),
(102, 'Diya Student', 'student', 'student123', NULL, NULL),
(201, 'Dr. Mehta', 'faculty', 'faculty123', 'OOP', 'Mon 10-12, Wed 2-4'),
(202, 'Prof. Sharma', 'faculty', 'faculty123', 'DBMS', 'Tue 11-1, Thu 1-3')
AS new
ON DUPLICATE KEY UPDATE
name = new.name,
role = new.role,
password = new.password,
subject = new.subject,
schedule = new.schedule;

INSERT INTO announcements (message, posted_by) VALUES
('Welcome to the Faculty & Student Interaction Portal.', 'System'),
('Faculty office hours are available every Friday at 2 PM.', 'Dr. Mehta');
