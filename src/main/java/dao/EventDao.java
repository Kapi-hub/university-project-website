package dao;

import misc.ConnectionFactory;
import models.RoleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum EventDao {
    instance;

    private final Connection connection;

    EventDao() {
        connection = ConnectionFactory.getConnection();
    }

    public void addEnrolment(int accountId, int eventId) throws SQLException {
        String query = "INSERT INTO event_enrollment VALUES (?, ?)";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);
        st.setInt(2, accountId);

        st.executeUpdate();
    }

    public int getRequiredCrewSize(RoleType role, int eventId) throws SQLException {
        String query = "SELECT crew_size FROM event_requirement WHERE event_id = ? AND role = ?::role_enum";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);
        st.setString(2, role.toString());

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

        throw new SQLException("Event not found");
    }

    public int getCurrentEnrolmentsForRole(RoleType role, int eventID) throws SQLException {
        String query = "SELECT COUNT(*) FROM crew_member c, event_enrollment ee WHERE ee.event_id = ? AND c.role = ?::role_enum AND c.id = ee.crew_member_id";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventID);
        st.setString(2, role.toString());
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

        throw new SQLException();
    }
}
