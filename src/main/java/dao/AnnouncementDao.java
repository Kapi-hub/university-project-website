package dao;

import misc.ConnectionFactory;
import models.AnnouncementBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;


public enum AnnouncementDao {
    instance;

    static Connection connection;

    public static void addAnnouncement(AnnouncementBean announcement) throws SQLException {
        String query = "INSERT INTO announcement (announcementID, announcer, title, body, dateTime) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, announcement.getAnnouncementID());
        st.setInt(2, announcement.getAnnouncer());
        st.setString(3, announcement.getTitle());
        st.setString(4, announcement.getBody());
        st.setTimestamp(5, announcement.getDateTime());
        st.executeUpdate();
    }

    public static void main(String[] args) throws SQLException {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        AnnouncementBean announcement = new AnnouncementBean(1, 12, "Test announcement 2", "Test Test Test Test", ts);
        addAnnouncement(announcement);
    }

    void AccountDao() {
        connection = ConnectionFactory.getConnection();
    }


}


