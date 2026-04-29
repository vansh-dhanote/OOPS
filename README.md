# Faculty & Student Interaction Portal

This is a standalone Java Swing desktop application built using core Object Oriented Programming concepts expected in a 2nd-year syllabus.

## Concepts Used

- Classes, Objects, Constructors
- Inheritance using `User`, `Student`, and `Faculty`
- Method overriding with `displayDashboard()`
- Interface using `PortalActions`
- Encapsulation through private fields with getters/setters
- Exception handling using `PortalException`
- Packages: `model`, `dao`, `ui`, `util`
- Collections: `ArrayList`, `HashMap`
- JDBC with MySQL
- Basic multithreading using `SwingWorker`
- Swing GUI with event handling

## Project Structure

- `src/model`
- `src/dao`
- `src/ui`
- `src/util`
- `sql/portal_schema.sql`
- `sql/portal_alter.sql`

## MySQL Setup

1. Open MySQL.
2. For a fresh database, run: `sql/portal_schema.sql`
3. For an already-created older database, run: `sql/portal_alter.sql`
4. Update database username/password in `src/util/DBConnection.java` if needed.

## MySQL JDBC Driver

Add MySQL Connector/J jar to your project build path before running.

Example jar name:

- `lib\mysql-connector-j-9.6.0.jar`

## Compile

```bash
javac -cp "lib\mysql-connector-j-9.6.0.jar" -d out src\MainApp.java src\model\*.java src\dao\*.java src\ui\*.java src\util\*.java
```

## Run

```bash
java -cp "out;lib\mysql-connector-j-9.6.0.jar" MainApp
```

## Sample Login

- Student: `101` / `student123`
- Faculty: `201` / `faculty123`

## Enhanced Features

- Subject-wise faculty selection while raising queries
- Faculty schedule display and availability check
- File attachment path storage using `JFileChooser`
- Query priority and submission timestamp
- `JTable` based query view with Java-side filtering
- Faculty-specific query handling and attachment opening
