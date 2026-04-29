package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaUpdater {
    public static void main(String[] args) {
        try {
            updateSchema();
            System.out.println("Database schema updated successfully.");
        } catch (Exception ex) {
            System.out.println("Schema update failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void updateSchema() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            if (!columnExists(connection, "users", "subject")) {
                executeSql(connection, "ALTER TABLE users ADD COLUMN subject VARCHAR(100) NULL");
            }

            if (!columnExists(connection, "users", "schedule")) {
                executeSql(connection, "ALTER TABLE users ADD COLUMN schedule VARCHAR(150) NULL");
            }

            if (!columnExists(connection, "queries", "faculty_id")) {
                executeSql(connection, "ALTER TABLE queries ADD COLUMN faculty_id INT NULL");
            }

            if (!columnExists(connection, "queries", "file_path")) {
                executeSql(connection, "ALTER TABLE queries ADD COLUMN file_path VARCHAR(255) NULL");
            }

            if (!columnExists(connection, "queries", "priority")) {
                executeSql(connection, "ALTER TABLE queries ADD COLUMN priority VARCHAR(20) NULL");
            }

            if (!columnExists(connection, "queries", "submitted_at")) {
                executeSql(connection, "ALTER TABLE queries ADD COLUMN submitted_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP");
            }

            if (!foreignKeyExists(connection, "queries", "fk_queries_faculty")) {
                executeSql(connection, "ALTER TABLE queries ADD CONSTRAINT fk_queries_faculty FOREIGN KEY (faculty_id) REFERENCES users(id)");
            }

            updateFacultyDetails(connection, 201, "OOP", "Mon 10-12, Wed 2-4");
            updateFacultyDetails(connection, 202, "DBMS", "Tue 11-1, Thu 1-3");
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
            return resultSet.next();
        }
    }

    private static boolean foreignKeyExists(Connection connection, String tableName, String foreignKeyName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getImportedKeys(connection.getCatalog(), null, tableName)) {
            while (resultSet.next()) {
                if (foreignKeyName.equalsIgnoreCase(resultSet.getString("FK_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void updateFacultyDetails(Connection connection, int id, String subject, String schedule) throws SQLException {
        String sql = "UPDATE users SET subject = ?, schedule = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subject);
            statement.setString(2, schedule);
            statement.setInt(3, id);
            statement.executeUpdate();
        }
    }

    private static void executeSql(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}
