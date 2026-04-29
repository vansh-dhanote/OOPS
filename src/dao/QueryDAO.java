package dao;

import model.Query;
import model.QueryActions;
import util.DBConnection;
import util.PortalException;
import util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryDAO implements QueryActions {
    @Override
    public void addQuery(Query query) throws PortalException {
        ValidationUtil.requireText(query.getSubject(), "Subject");
        ValidationUtil.requireText(query.getQuestion(), "Description");
        ValidationUtil.requireText(query.getPriority(), "Priority");

        if (query.getFacultyId() <= 0) {
            throw new PortalException("Please select a faculty member.");
        }

        String sql = "INSERT INTO queries (student_id, faculty_id, subject, question, answer, status, file_path, priority, submitted_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, query.getStudentId());
            statement.setInt(2, query.getFacultyId());
            statement.setString(3, query.getSubject());
            statement.setString(4, query.getQuestion());
            statement.setString(5, query.getAnswer());
            statement.setString(6, query.getStatus());
            statement.setString(7, query.getFilePath());
            statement.setString(8, query.getPriority());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new PortalException("Unable to save query.", ex);
        }
    }

    public ArrayList<Query> getQueriesByStudent(int studentId) throws PortalException {
        ArrayList<Query> queries = new ArrayList<Query>();
        String sql = "SELECT q.id, q.student_id, q.faculty_id, su.name AS student_name, fu.name AS faculty_name, q.subject, " +
                "q.question, q.answer, q.status, q.file_path, q.priority, q.submitted_at " +
                "FROM queries q " +
                "JOIN users su ON q.student_id = su.id " +
                "JOIN users fu ON q.faculty_id = fu.id " +
                "WHERE q.student_id = ? ORDER BY q.submitted_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                queries.add(buildQuery(resultSet));
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to fetch student queries.", ex);
        }
        return queries;
    }

    public ArrayList<Query> getAllQueries() throws PortalException {
        ArrayList<Query> queries = new ArrayList<Query>();
        String sql = "SELECT q.id, q.student_id, q.faculty_id, su.name AS student_name, fu.name AS faculty_name, q.subject, " +
                "q.question, q.answer, q.status, q.file_path, q.priority, q.submitted_at " +
                "FROM queries q " +
                "JOIN users su ON q.student_id = su.id " +
                "JOIN users fu ON q.faculty_id = fu.id " +
                "ORDER BY q.submitted_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                queries.add(buildQuery(resultSet));
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to fetch queries.", ex);
        }
        return queries;
    }

    public ArrayList<Query> getQueriesByFaculty(int facultyId) throws PortalException {
        ArrayList<Query> queries = new ArrayList<Query>();
        String sql = "SELECT q.id, q.student_id, q.faculty_id, su.name AS student_name, fu.name AS faculty_name, q.subject, " +
                "q.question, q.answer, q.status, q.file_path, q.priority, q.submitted_at " +
                "FROM queries q " +
                "JOIN users su ON q.student_id = su.id " +
                "JOIN users fu ON q.faculty_id = fu.id " +
                "WHERE q.faculty_id = ? ORDER BY q.submitted_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, facultyId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                queries.add(buildQuery(resultSet));
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to fetch faculty queries.", ex);
        }
        return queries;
    }

    @Override
    public void replyToQuery(int queryId, String answer, boolean resolved) throws PortalException {
        ValidationUtil.requireText(answer, "Reply");

        String sql = "UPDATE queries SET answer = ?, status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, answer);
            statement.setString(2, resolved ? "Answered" : "Pending");
            statement.setInt(3, queryId);

            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new PortalException("Selected query does not exist.");
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to update query.", ex);
        }
    }

    @Override
    public void markResolved(int queryId) throws PortalException {
        String sql = "UPDATE queries SET status = 'Resolved' WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, queryId);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new PortalException("Selected query does not exist.");
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to mark query as resolved.", ex);
        }
    }

    private Query buildQuery(ResultSet resultSet) throws SQLException {
        return new Query(
                resultSet.getInt("id"),
                resultSet.getInt("student_id"),
                resultSet.getInt("faculty_id"),
                resultSet.getString("student_name"),
                resultSet.getString("faculty_name"),
                resultSet.getString("subject"),
                resultSet.getString("question"),
                resultSet.getString("answer"),
                resultSet.getString("status"),
                resultSet.getString("file_path"),
                resultSet.getString("priority"),
                resultSet.getTimestamp("submitted_at")
        );
    }
}
