package dao;

import misc.ConnectionFactory;
import models.AnnouncementResponseBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public enum AnnouncementDao {
    instance;

    private final Connection connection;

    AnnouncementDao() {
        connection = ConnectionFactory.getConnection();
    }

    public AnnouncementResponseBean[] getAnnouncementsForCrew(int crewMemberId) throws SQLException {
        String query = "SELECT * FROM announcement WHERE recipient = ? OR recipient IS NULL ORDER BY date_time DESC";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, crewMemberId);

        ResultSet rs = st.executeQuery();

        ArrayList<AnnouncementResponseBean> announcements = new ArrayList<>();

        while (rs.next()) {
            AnnouncementResponseBean announcement = new AnnouncementResponseBean();
            announcement.setId(rs.getInt("id"));
            announcement.setAnnouncer(AccountDao.instance.getName(rs.getInt("announcer_id")));
            announcement.setTitle(rs.getString("title"));
            announcement.setBody(rs.getString("body"));
            announcement.setDateTime(rs.getTimestamp("date_time"));
            announcement.setIsPersonal(rs.getInt("recipient") == crewMemberId);
            announcement.setUrgent(rs.getBoolean("urgent"));
            announcements.add(announcement);
        }

        return announcements.size() == 0 ? null : announcements.toArray(new AnnouncementResponseBean[0]);
    }
}
