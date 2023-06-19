package dao;

import misc.ConnectionFactory;
import models.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public void removeEnrolment(int accountId, int eventId) throws SQLException {
        String query = "DELETE FROM event_enrollment WHERE event_id = ? AND crew_member_id = ?";

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

        throw new SQLException("No enrolments");
    }

    public void overwriteRequired(int eventId, RoleType role, int amount) throws SQLException {
        String query = "INSERT INTO event_requirement (event_id, crew_size, role)" +
                "VALUES (?, ?, ?::role_enum) " +
                "ON CONFLICT (event_id, role) " +
                "DO UPDATE SET crew_size = EXCLUDED.crew_size;";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);
        st.setInt(2, amount);
        st.setString(3, role.toString());

        st.executeUpdate();
    }

    public EventBean[] getFromMonth(Timestamp timestamp) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(timestamp);
        long timeToNextMonth = switch (dateStr.substring(5, 7)) {
            case "02" -> TimeUnit.DAYS.toMillis(28);
            case "04", "06", "09", "11" -> TimeUnit.DAYS.toMillis(30);
            default -> TimeUnit.DAYS.toMillis(31);
        };
        Timestamp nextMonthTimestamp = new Timestamp(timestamp.getTime() + timeToNextMonth);
        String nextMonthDateStr = sdf.format(nextMonthTimestamp);

        String query = "SELECT * FROM event WHERE DATE(start) >= TO_DATE(?, 'YYYY-MM-DD') AND DATE(start) < TO_DATE(?, 'YYYY-MM-DD') AND (status = 'done'::status OR status = 'review'::status) ORDER BY start";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, dateStr);
        st.setString(2, nextMonthDateStr);

        ResultSet rs = st.executeQuery();

        ArrayList<ArrayList<Object>> rsList = new ArrayList<>();

        int count;
        for (count = 0; rs.next(); count++) {
            ArrayList<Object> rowData = new ArrayList<>();
            rowData.add(rs.getInt("id"));
            rowData.add(rs.getInt("client_id"));
            rowData.add(rs.getString("name"));
            rowData.add(rs.getString("description"));
            rowData.add(rs.getTimestamp("start"));
            rowData.add(rs.getInt("duration"));
            rowData.add(rs.getString("location"));
            rowData.add(rs.getInt("production_manager_id"));
            rowData.add(EventType.toEnum(rs.getString("type")));
            rowData.add(BookingType.toEnum(rs.getString("booking_type")));
            rowData.add(EventStatus.toEnum(rs.getString("status")));
            rsList.add(rowData);
        }

        if (count == 0) {
            return null;
        }

        EventBean[] events = new EventBean[count];
        int i = 0;
        for (ArrayList<Object> row : rsList) {
            events[i] = new EventBean((int) row.get(0), (int) row.get(1), (String) row.get(2), (String) row.get(3), (Timestamp) row.get(4), (int) row.get(5), (String) row.get(6), (int) row.get(7), (EventType) row.get(8), (BookingType) row.get(9), (EventStatus) row.get(10));
            i++;
        }
        return events;
    }

    public EventStatus getEventStatus(int eventId) throws SQLException {
        String query = "SELECT status FROM event WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return EventStatus.toEnum(rs.getString(1));
        }

        throw new SQLException("Event not found");
    }

    public Object[] getEnrolled(int eventId) {
        String query = "SELECT crew_member_id FROM event_enrollment WHERE event_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, eventId);

            ResultSet rs = st.executeQuery();

            ArrayList<Integer> enrolledIds = new ArrayList<>();
            while (rs.next()) {
                enrolledIds.add(rs.getInt(1));
            }

            ArrayList<RoleType> rolesPerId = new ArrayList<>();
            for (int id : enrolledIds) {
                rolesPerId.add(CrewMemberDao.I.getRole(id));
            }

            Map<RoleType, ArrayList<String>> roleMap = new HashMap<>();
            for (int i = 0; i < rolesPerId.size(); i++) {
                RoleType currentRoletype = rolesPerId.get(i);
                String currentName = AccountDao.instance.getName(enrolledIds.get(i));

                ArrayList<String> toBeAssigned;

                if (roleMap.containsKey(currentRoletype)) {
                    toBeAssigned = roleMap.get(currentRoletype);
                } else {
                    toBeAssigned = new ArrayList<>();
                }

                toBeAssigned.add(currentName);
                roleMap.put(currentRoletype, toBeAssigned);
            }
            Object[] returnValue = new Object[roleMap.size()];

            int i = 0;
            for (RoleType role : roleMap.keySet()) {
                ArrayList<String> memberArrayList = roleMap.get(role);
                String[] members = new String[memberArrayList.size()];
                for (int j = 0; j < memberArrayList.size(); j++) {
                    members[j] = memberArrayList.get(j);
                }
                Object[] innerObjectArray = new Object[2];
                innerObjectArray[0] = role;
                innerObjectArray[1] = members;
                returnValue[i] = innerObjectArray;
                i++;
            }
            return returnValue;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            // No error handling needed as the next line is return null anyway.
        }
        return null;
    }

    public Map<RoleType, Integer> getRequiredMap(int id) throws SQLException {
        String query = "SELECT role, crew_size FROM event_requirement WHERE event_id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();

        Map<RoleType, Integer> returnValue = new HashMap<>();

        while (rs.next()) {
            returnValue.put(RoleType.toEum(rs.getString(1)), rs.getInt(2));
        }
        return returnValue;
    }

    public boolean isEnrolled(int crewId, int eventId) {
        String query = "SELECT 1 FROM event_enrollment WHERE crew_member_id = ? AND event_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, crewId);
            st.setInt(2, eventId);

            ResultSet rs = st.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean isEventInPast(int eventId) throws SQLException {
        String query = "SELECT 1 FROM event WHERE id = ? AND start < NOW()";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);

        ResultSet rs = st.executeQuery();
        return rs.next();
    }
}
