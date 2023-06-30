package dao;

import misc.ConnectionFactory;
import models.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class to handle connectivity with the database related to events
 */
public enum EventDao {
    instance;

    private final Connection connection;

    /**
     * Setting up of the connection
     */
    EventDao() {
        connection = ConnectionFactory.getConnection();
    }

    /**
     * Enrolls account with event
     * @param accountId account that enrolls
     * @param eventId the event the account enrolls in
     * @throws SQLException occurs when SQL error
     */
    public void addEnrolment(int accountId, int eventId) throws SQLException {
        String query = "INSERT INTO event_enrollment VALUES (?, ?)";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);
        st.setInt(2, accountId);

        st.executeUpdate();
    }

    /**
     * removes enrolls of account
     * @param accountId account that unenrolls
     * @param eventId the event it unenrolls in
     * @throws SQLException occurs when SQL error
     */
    public void removeEnrolment(int accountId, int eventId) throws SQLException {
        String query = "DELETE FROM event_enrollment WHERE event_id = ? AND crew_member_id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);
        st.setInt(2, accountId);

        st.executeUpdate();
    }

    /**
     * Get the required crew_size for a role given
     * @param role role given
     * @param eventId the event in question
     * @return the number of people needed for that role
     * @throws SQLException occurs when SQL error
     */
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

    /**
     * Returns the amount of people enrolled for a specific role and event
     * @param role the role in question
     * @param eventID the event in question
     * @return the amount of people enrolled
     * @throws SQLException occurs when SQL error.
     */
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

    /**
     * Update the required crew members needed.
     * @param eventId the event in question
     * @param role the role in question
     * @param amount the value it will change it to
     * @throws SQLException occurs when an SQL error.
     */
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

    /**
     * Get all information of a specific eventBean
     * @param eventId the event ID in question
     * @return all information ranging from event_id to status of the event
     * @throws SQLException happens when an SQL error occurs
     */
    public EventBean[] getAllDetails(int eventId) throws SQLException {
        String query = "SELECT * FROM event WHERE id = ?";
        try {

            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, eventId);
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


        } catch (SQLException e) {
            System.err.println(e);
        }
        return null;

    }

    /**
     * Gets all events from a specific month
     * @param timestamp the timestamp it needs the month from
     * @return all the events in the month.
     * @throws SQLException occurs when an SQL error is there.
     */
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

    /**
     * Gets the event status of a given event
     * @param eventId the event in question
     * @return the event status
     * @throws SQLException occurs when there is an SQL error.
     */
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

    /**
     * Get all enrolled members of a given event
     * @param eventId the event in question
     * @return all enrolled members
     */
    public Object[] getEnrolledMembers(int eventId) {
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

            Map<RoleType, ArrayList<Object[]>> roleMap = new HashMap<>();
            for (int i = 0; i < rolesPerId.size(); i++) {
                RoleType currentRoletype = rolesPerId.get(i);
                int currentId = enrolledIds.get(i);
                String currentName = AccountDao.instance.getName(currentId);

                ArrayList<Object[]> toBeAssigned;

                if (roleMap.containsKey(currentRoletype)) {
                    toBeAssigned = roleMap.get(currentRoletype);
                } else {
                    toBeAssigned = new ArrayList<>();
                }

                toBeAssigned.add(new Object[] {currentId, currentName});
                roleMap.put(currentRoletype, toBeAssigned);
            }
            Object[] returnValue = new Object[roleMap.size()];

            int i = 0;
            for (RoleType role : roleMap.keySet()) {
                ArrayList<Object[]> memberArrayList = roleMap.get(role);
                Object[] members = new Object[memberArrayList.size()];
                for (int j = 0; j < memberArrayList.size(); j++) {
                    members[j] = memberArrayList.get(j);
                }
                Object[] innerObjectArray = new Object[] {role, members};
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

    /**
     * Gets the information of the event that a person has also enrolled in.
     * @param accountId the account id that requests it
     * @param client an optional extra filter to specify the client ID
     * @param month an optional extra filter to specify the month
     * @return the list of events
     * @throws SQLException occurs when SQL error.
     */
    public EventBean[] getEnrolledEvents(int accountId, int client, int month) throws SQLException {
        String query = """
            SELECT e.id, e.client_id, e.name, e.description, e.start, e.duration, 
            e.location, e.production_manager_id, e.type, e.booking_type, e.status 
            FROM event e, event_enrollment ee 
            WHERE 
              ee.crew_member_id = ? AND 
              ee.event_id = e.id
            """;
        List<Integer> params = new ArrayList<>();
        params.add(accountId);
        if (client != 0) {
            query += " AND e.client_id = ?";
            params.add(client);
        }
        if (month != 0) {
            query += " AND EXTRACT(MONTH FROM e.start) = ?";
            params.add(month);
        }
        PreparedStatement st = connection.prepareStatement(query);
        int index = 1;
        for (int param : params) {
            st.setInt(index++, param);
        }
        ResultSet rs = st.executeQuery();

        ArrayList<EventBean> eventList = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            int client_id = rs.getInt("client_id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            Timestamp start = rs.getTimestamp("start");
            int duration = rs.getInt("duration");
            String location = rs.getString("location");
            int production_manager_id = rs.getInt("production_manager_id");
            EventType type = EventType.toEnum(rs.getString("type"));
            BookingType booking_type = BookingType.toEnum(rs.getString("booking_type"));
            EventStatus status = EventStatus.toEnum(rs.getString("status"));

            eventList.add(new EventBean(id, client_id, name, description, start, duration, location,
                    production_manager_id, type, booking_type, status));
        }

        EventBean[] returnValue = new EventBean[eventList.size()];
        return eventList.size() == 0 ? null : eventList.toArray(returnValue);
    }

    /**
     * Returns a map of all the required roles
     * @param id the event ID in question
     * @return a map (RoleType -> Amount of people needed)
     * @throws SQLException occurs when SQL error occurs.
     */
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

    /**
     * Checks whether a crew member has been enrolled or not.
     * @param crewId the crew member in question
     * @param eventId the event in question
     * @return true if enrolled otherwise false.
     */
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

    /**
     * A method to check whether an event is in the path
     * @param eventId the event in question
     * @return true if the event is in the past
     * @throws SQLException occurs when an SQL error
     */
    public boolean isEventInPast(int eventId) throws SQLException {
        String query = "SELECT 1 FROM event WHERE id = ? AND start < NOW()";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, eventId);

        ResultSet rs = st.executeQuery();
        return rs.next();
    }

    /**
     * Retrieves the count of events based on the provided account ID and account type.
     * @param accountId The ID of the account for which to retrieve event count.
     * @param type The type of the account (ADMIN or CREW_MEMBER).
     * @return The count of events based on the provided account ID and account type.
     */
    public int getEventCount(int accountId, AccountType type) {
        String query;
        switch (type) {
            case ADMIN -> query = "SELECT COUNT(*) FROM event WHERE status = 'to do'::status";
            case CREW_MEMBER ->
                    query = "SELECT COUNT(*) FROM event_enrollment ee, event e WHERE ee.event_id = e.id AND ee.crew_member_id = ? AND e.start::date BETWEEN NOW()::date AND NOW()::date + '1 day'::interval";
            default -> {
                return -1;
            }
        }
        try {
            PreparedStatement st = connection.prepareStatement(query);
            if (type == AccountType.CREW_MEMBER) {
                st.setInt(1, accountId);
            }

            ResultSet rs = st.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**

     Retrieves the total hours worked by a crew member for each month and year, based on the provided crew ID.
     @param crewId The ID of the crew member for which to retrieve the hours worked.
     @return An array of Object arrays containing the total hours worked and corresponding month-year for each entry.
     @throws SQLException if a database access error occurs
     */
    public Object[] getHoursWorked(int crewId) throws SQLException {
        String query = """
                SELECT SUM(e.duration), CONCAT(m.month, '-', m.year)
                FROM event e, event_enrollment ee, (
                    SELECT id, EXTRACT (MONTH FROM start) AS month, EXTRACT (YEAR FROM start) AS year
                    FROM event
                ) AS m
                WHERE (
                    e.id = ee.event_id AND
                    ee.crew_member_id = ? AND
                    e.start < NOW() AND
                    m.id = e.id
                )
                GROUP BY (m.month, m.year)
                ORDER BY m.year DESC, m.month DESC""";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, crewId);

        ResultSet rs = st.executeQuery();

        ArrayList<Object[]> resultList = new ArrayList<>();

        while (rs.next()) {
            String date = rs.getString(2);
            int hours = rs.getInt(1);
            resultList.add(new Object[]{date, hours});
        }

        return resultList.size() == 0 ? null : resultList.toArray(new Object[0]);
    }
}
