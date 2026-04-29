# Faculty & Student Interaction Portal Run Guide

This file explains how to run the project from start to finish, including MySQL database setup.

## 1. Requirements

- Java JDK 17 or later
- MySQL Server
- MySQL Workbench

## 2. Project Location

Project folder:

`C:\Users\priva\OneDrive\Documents\codexpro\FacultyStudentInteractionPortal`

## 3. Database Setup

### Option A: Fresh database setup

Open MySQL Workbench and run:

`sql/portal_schema.sql`

This will:

- create the database `faculty_student_portal`
- create the tables `users`, `queries`, and `announcements`
- insert sample student and faculty records

### Option B: If you already created the older database version

Run:

`sql/portal_alter.sql`

This will add the new columns:

- `users.subject`
- `users.schedule`
- `queries.faculty_id`
- `queries.file_path`
- `queries.priority`
- `queries.submitted_at`

## 4. Update Database Credentials

Open:

`src/util/DBConnection.java`

Check these values:

```java
private static final String URL = "jdbc:mysql://localhost:3306/faculty_student_portal";
private static final String USERNAME = "root";
private static final String PASSWORD = "vanshvansh";
```

If your MySQL username or password is different, update this file before running the project.

## 5. JDBC Driver

The MySQL JDBC driver is already included in:

`lib/mysql-connector-j-9.6.0.jar`

So you do not need to download it again for this project.

## 6. Compile the Project

Open PowerShell in the project folder and run:

```powershell
javac -cp "lib\mysql-connector-j-9.6.0.jar" -d out src\MainApp.java src\model\*.java src\dao\*.java src\ui\*.java src\util\*.java
```

## 7. Run the Project

### Easy method

Double-click:

`run_portal.bat`

### PowerShell method

```powershell
.\run_portal.bat
```

### Manual Java run command

```powershell
java -cp "out;lib\mysql-connector-j-9.6.0.jar" MainApp
```

## 8. If Schema Update Is Needed Through Java

This project also includes:

`src/util/SchemaUpdater.java`

It can update the database schema using JDBC if required.

Run:

```powershell
java -cp "out;lib\mysql-connector-j-9.6.0.jar" util.SchemaUpdater
```

## 9. Sample Login Credentials

### Student

- User ID: `101`
- Password: `student123`

### Faculty

- User ID: `201`
- Password: `faculty123`

## 10. Main Features

- Student login and faculty login
- Subject-wise faculty selection
- Faculty schedule and availability check
- Query raising with priority and file attachment path
- Query reply and resolved status
- Announcements module
- Query filtering using Swing table view

## 11. Common Problems

### Database connection error

Check:

- MySQL server is running
- database name is `faculty_student_portal`
- username/password in `DBConnection.java` are correct

### Login not working

Check whether you executed `portal_schema.sql` correctly and sample users were inserted.

### GUI opens but data does not load

This usually means:

- database tables are missing
- schema update was not applied
- MySQL credentials are incorrect

## 12. Important Files

- `src/MainApp.java` : main entry point
- `src/ui/LoginFrame.java` : login screen
- `src/ui/StudentDashboard.java` : student interface
- `src/ui/FacultyDashboard.java` : faculty interface
- `src/util/DBConnection.java` : database connection settings
- `sql/portal_schema.sql` : fresh database setup
- `sql/portal_alter.sql` : alter script for old database
- `run_portal.bat` : quick run file
