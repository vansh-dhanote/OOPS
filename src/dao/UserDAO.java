package dao;

import model.Faculty;
import model.Student;
import model.User;
import util.DBConnection;
import util.PortalException;
import util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserDAO {
    // HashMap is used as a simple cache of users already authenticated.
    private final HashMap<Integer, User> userCache = new HashMap<Integer, User>();

    public User authenticateUser(String userIdText, String password) throws PortalException {
        ValidationUtil.requireText(userIdText, "User ID");
        ValidationUtil.requireText(password, "Password");

        int userId;
        try {
            userId = Integer.parseInt(userIdText.trim());
        } catch (NumberFormatException ex) {
            throw new PortalException("User ID must be a number.");
        }

        String sql = "SELECT id, name, role, password, subject, schedule FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new PortalException("User not found.");
            }

            String role = resultSet.getString("role");
            User user;

            if ("student".equalsIgnoreCase(role)) {
                user = new Student(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        role,
                        resultSet.getString("password")
                );
            } else {
                user = new Faculty(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        role,
                        resultSet.getString("password"),
                        resultSet.getString("subject"),
                        resultSet.getString("schedule")
                );
            }

            if (!user.login(password)) {
                throw new PortalException("Invalid password.");
            }

            userCache.put(user.getId(), user);
            return user;
        } catch (SQLException ex) {
            throw new PortalException("Database error while logging in.", ex);
        }
    }

    public HashMap<Integer, User> getUserCache() {
        return userCache;
    }

    public ArrayList<String> getAvailableSubjects() throws PortalException {
        ArrayList<String> subjects = new ArrayList<String>();
        String sql = "SELECT DISTINCT subject FROM users WHERE role = 'faculty' AND subject IS NOT NULL AND subject <> '' ORDER BY subject";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                subjects.add(resultSet.getString("subject"));
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to load subjects.", ex);
        }
        return subjects;
    }

    public ArrayList<Faculty> getFacultyBySubject(String subject) throws PortalException {
        ValidationUtil.requireText(subject, "Subject");

        ArrayList<Faculty> faculties = new ArrayList<Faculty>();
        String sql = "SELECT id, name, role, password, subject, schedule FROM users WHERE role = 'faculty' AND subject = ? ORDER BY name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, subject);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                faculties.add(new Faculty(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("role"),
                        resultSet.getString("password"),
                        resultSet.getString("subject"),
                        resultSet.getString("schedule")
                ));
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to load faculty list.", ex);
        }
        return faculties;
    }
}
