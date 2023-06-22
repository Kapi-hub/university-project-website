package dao;

import misc.ConnectionFactory;
import models.*;

import java.sql.*;
import java.util.List;


public enum AdminDao {
    I;
    final Connection connection;

    AdminDao() {
        connection = ConnectionFactory.getConnection();
    }

    public void createNewMember(CrewMemberBean crewMember) {
        //Create new account
        String insertAccountQuery = "INSERT INTO account(forename, surname, username, email_address, password, type) VALUES (?,?, ?, ?, ?, ?::account_type_enum)";
        try {
            PreparedStatement st = connection.prepareStatement(insertAccountQuery);
            st.setString(1, crewMember.getForename());
            st.setString(2, crewMember.getSurname());
            st.setString(3, crewMember.getUsername());
            st.setString(4, crewMember.getEmailAddress());
            st.setString(5, crewMember.getPassword());
            st.setString(6, crewMember.getAccountType().toString());

            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("In insertion of account " + e);
        }

        //Get the account's id so that it will be used for the foreign key of crew_member
        int accountId = 0;
        String getAccountId = "SELECT id FROM account WHERE username LIKE ?;";

        try {
            PreparedStatement st = connection.prepareStatement(getAccountId);
            System.out.println(crewMember.getUsername());
            st.setString(1, crewMember.getUsername());
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {

                    accountId = resultSet.getInt("id");
                    System.out.println(accountId);
                }
            }
        } catch (SQLException e) {
            System.out.println("error getting the account id");
        }

        //Create crew_member
        String insertCrewQuery = "INSERT INTO crew_member(id, role, team) VALUES(?, ?::role_enum, ?::team_enum)";
        try {
            PreparedStatement st = connection.prepareStatement(insertCrewQuery);
            st.setInt(1, accountId);
            st.setString(2, crewMember.getRole().toString());
            st.setString(3, crewMember.getTeam().toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("in insertion of crewMember " + e);
        }
    }

    public int addClient(ClientBean client) throws SQLException {
        String query = "INSERT INTO account (forename, surname, username, email_address, type) VALUES (?,?,?,?,'client'::account_type_enum) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, client.getForename());
        st.setString(2, client.getSurname());
        st.setString(3, client.getUsername());
        st.setString(4, client.getEmailAddress());
        ResultSet rs = st.executeQuery();

        int client_id = -1;
        if (rs.next()) {
            client_id = rs.getInt(1);
        }
        System.out.printf("===SQL=== ADDED A CLIENT TO ACCOUNT TABLE, RETURNED ID %s.\n", client_id);
        query = "INSERT INTO client (id, phone_number) VALUES (?, ?)";
        st = connection.prepareStatement(query);
        st.setInt(1, client_id);
        st.setString(2, client.getPhone_number());
        st.executeUpdate();
        System.out.printf("===SQL=== ADDED A CLIENT TO CLIENT TABLE WITH ID %s\n", client_id);
        return client_id;
    }

    public int addEvent(EventBean event) throws SQLException {
        String query = "INSERT INTO event (client_id, name, description, start, duration, location, type, booking_type) VALUES (?,?,?,?,?,?, ?::event_type_enum, ?::booking_type_enum) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, event.getClient_id());
        st.setString(2, event.getName());
        st.setString(3, event.getDescription());
        st.setTimestamp(4, event.getStart());
        st.setInt(5, event.getDuration());
        st.setString(6, event.getLocation());
        st.setString(7, event.getType().toString());
        st.setString(8, event.getBooking_type().toString());


        ResultSet rs = st.executeQuery();
        int event_id = -1;
        if (rs.next())
            event_id = rs.getInt(1);
        System.out.printf("===SQL=== ADDED AN EVENT TO DATABASE RETURNING ID %s\n", event_id);
        return event_id;
    }

    public void addRequirement(RequiredCrewBean required) throws SQLException {
        String query = "INSERT INTO event_requirement (event_id, crew_size, role) VALUES (?, ?, ?::role_enum)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, required.getEvent_id());
        st.setInt(2, required.getCrew_size());
        st.setString(3, required.getRole().toString());
        st.executeUpdate();
        System.out.printf("===SQL=== ADDED A ROLE REQUIREMENT WITH VALUES %s, %s, %s\n",
                required.getEvent_id(), required.getCrew_size(), required.getRole().toString());
    }

//    public void createNewEvent(EventBean event) {
//        String insertEventQuery = "INSERT INTO event(id, name, description, start, duration, location, type) VALUES (?,?,?,?,?,?::event_type_enum)";
//        try {
//            PreparedStatement st = connection.prepareStatement(insertEventQuery);
//            st.setInt(1, event.getId());
//            st.setString(2, event.getName());
//            st.setString(3, event.getDescription());
//            st.setTimestamp(4, event.getStart());
//            st.setString(5, event.getLocation());
//            st.setString(6, event.getType().toString());
//            st.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println(e);
//        }
//    }



    public String getAllCrewMembers() throws SQLException {
        String query = "SELECT json_agg(jsonb_build_object('member_id', a.id, 'member_forename', " +
                "a.forename, 'member_surname', a.surname) FROM account a " +
                "WHERE a.type='crew_member') AS crews";

        return getSQLString(query);
    }

    public String getProducers() throws SQLException {
        String query = "SELECT a.forename, a.surname " +
                "FROM account a, crew_member" +
                "WHERE a.id = c.id AND c.role = 'producer'";
        return getSQLString(query);

    }

    public void addRequirement(List<RequiredCrewBean> required) throws SQLException {
        for (RequiredCrewBean requiredCrewBean : required) {
            String query = "INSERT INTO event_requirement (event_id, crew_size, role) VALUES (?, ?, ?::role_enum)";
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, requiredCrewBean.getEvent_id());
            st.setInt(2, requiredCrewBean.getCrew_size());
            st.setString(3, requiredCrewBean.getRole().toString());
            st.executeUpdate();
        }
    }
    public void addAnnouncement(AnnouncementBean announcement) throws SQLException {
        String insertQuery = "INSERT INTO announcement(announcer_id,title,body,recipient) VALUES (?,?,?,?)";
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1, announcement.getAnnouncer());
        st.setString(2, announcement.getTitle());
        st.setString(3, announcement.getBody());
        st.setInt(4, announcement.getRecipient());
        st.executeUpdate();
    }

    public String getNotFullEvents() throws SQLException {
        String insertQuery =
                """
                SELECT json_agg(DISTINCT jsonb_build_object(
                    'event_id', e.id,
                    'event_title', e.name
                )) AS result
                FROM event e
                JOIN event_requirement er ON e.id = er.event_id
                LEFT JOIN (
                    SELECT event_id, COUNT(*) AS enrollments
                    FROM event_enrollment
                    GROUP BY event_id
                ) ee ON e.id = ee.event_id
                WHERE (ee.enrollments < er.crew_size OR ee.enrollments IS NULL)
                  AND e.id IS NOT NULL
                  AND e.name IS NOT NULL;
                """;
        return getSQLString(insertQuery);
    }

    public String getAllAnnouncements() throws SQLException {
        String insertQuery = """
                SELECT json_agg(jsonb_build_object(
                    'announcement_id', subquery.id,
                    'announcement_title', subquery.title,
                    'announcement_body', subquery.body,
                    'announcement_timestamp', subquery.date_time,
                    'announcer', json_build_object(
                        'forename', subquery.forename,
                        'surname', subquery.surname
                    )
                )) AS result
                FROM (
                    SELECT ann.id, ann.title, ann.body, ann.date_time, a.forename, a.surname
                    FROM announcement ann
                    JOIN account a ON ann.announcer_id = a.id
                    ORDER BY ann.id DESC
                    LIMIT 10
                ) subquery;
                """;

        return getSQLString(insertQuery);
    }

    public String getLatestEvent() throws SQLException {
        String insertQuery = """
                SELECT json_agg(json_build_object(
                    'name',name,
                    'description',description,
                    'start',start,
                    'duration',duration,
                    'type',type
                        ) 
                     )AS result
                    From event
                    """;//TODO make it show latest as group by order by won't give one row
        return getSQLString(insertQuery);
    }

    private String getSQLString(String insertQuery) throws SQLException {
        PreparedStatement st = connection.prepareStatement(insertQuery);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String result = rs.getString(1);
            rs.close();
            st.close();
            return result;
        }
        rs.close();
        st.close();
        return null;
    }

    public String getUser(int accountId) throws SQLException {
        String insertQuery = """
                Select forename 
                From account
                Where id = ?;
                """;
        PreparedStatement st = connection.prepareStatement(insertQuery) ;
        st.setInt(1,accountId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String result = rs.getString(1);
            rs.close();
            st.close();
            return result;
        }
        rs.close();
        st.close();
        return null;
    }
}

