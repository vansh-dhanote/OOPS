package dao;

import model.Announcement;
import util.DBConnection;
import util.PortalException;
import util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AnnouncementDAO {
    public void addAnnouncement(Announcement announcement) throws PortalException {
        ValidationUtil.requireText(announcement.getMessage(), "Announcement");

        String sql = "INSERT INTO announcements (message, posted_by) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, announcement.getMessage());
            statement.setString(2, announcement.getPostedBy());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new PortalException("Unable to post announcement.", ex);
        }
    }

    public ArrayList<Announcement> getAllAnnouncements() throws PortalException {
        ArrayList<Announcement> announcements = new ArrayList<Announcement>();
        String sql = "SELECT id, message, posted_by FROM announcements ORDER BY id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                announcements.add(new Announcement(
                        resultSet.getInt("id"),
                        resultSet.getString("message"),
                        resultSet.getString("posted_by")
                ));
            }
        } catch (SQLException ex) {
            throw new PortalException("Unable to fetch announcements.", ex);
        }
        return announcements;
    }
}
