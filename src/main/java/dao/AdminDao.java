package dao;

import jdk.jfr.Event;
import misc.ConnectionFactory;
import models.*;

import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.ArrayList;

import static misc.Security.encodeSalt;


public enum AdminDao {
    I;
    final Connection connection;

    AdminDao() {
        connection = ConnectionFactory.getConnection();
    }

    /*METHODS RELATED TO EVENTS*/
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

    public ArrayList<EventBean> getNotFullEvents() throws SQLException {
        String insertQuery = """
                SELECT e.id, e.name 
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

    public String getLatestEvent() throws SQLException {
        String insertQuery = """
                SELECT json_agg(
                               json_build_object(
                                       'eventDetails', json_build_object(
                                       'id', e.id,
                                       'name', e.name,
                                       'description', e.description,
                                       'start', e.start,
                                       'duration', e.duration,
                                       'location', e.location,
                                       'type', e.type,
                                       'booking_type', e.booking_type,
                                       'clients', (SELECT json_agg(
                                                                  json_build_object(
                                                                          'forename', a.forename,
                                                                          'surname', a.surname,
                                                                          'emailAddress', a.email_address,
                                                                          'phone_number', c.phone_number
                                                                      ))
                                                   FROM shotmaniacs1.account a
                                                            JOIN shotmaniacs1.client c ON a.id = c.id
                                                   WHERE a.type = 'client'
                                                     AND c.id = e.client_id),
                                       'requirements',
                                       (SELECT json_agg(json_build_object('role', role, 'crew_size', crew_size)) AS json_data
                                        FROM shotmaniacs1.event_requirement r
                                        WHERE e.id = r.event_id
                                        GROUP BY r.event_id)
                ))
                           ) AS result
                FROM shotmaniacs1.event e
                 """;
        return getSQLString(insertQuery);
    }

    public String getEventWithId(int id) throws SQLException {
        String query = "SELECT json_agg(" +
                "json_build_object(" +
                "'id', id," +
                "'name', name," +
                "'description', description," +
                "'start', start," +
                "'duration', duration," +
                "'location', location," +
                "'type', type," +
                "'booking_type', booking_type))" +
                "FROM shotmaniacs1.event " +
                "WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(query);) {
            st.setInt(1, id);
            return getSQLString(st.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void changeEventDetails(EventBean event) {
        String updateQuery = "UPDATE event SET name=?, description=?, start=?, duration=?, location=?, type=?, " +
                "booking_type=? WHERE id=?";
        try {
            PreparedStatement st = connection.prepareStatement(updateQuery);
            st.setString(1, event.getName());
            st.setString(2, event.getDescription());
            st.setTimestamp(3, event.getStart());
            st.setInt(4, event.getDuration());
            st.setString(5, event.getLocation());
            st.setString(6, event.getType().toString());
            st.setString(7, event.getBooking_type().toString());
            st.setInt(8, event.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEvent(int id) throws SQLException {
        String query = """
                DELETE FROM event" +
                "WHERE id = ?""";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, id);

        st.executeUpdate();
    }

    /*METHODS RELATED TO CREW MEMBERS*/

    public void createNewMember(CrewMemberBean crewMember) throws GeneralSecurityException {
        String[] passwords = encodeSalt(crewMember.getPassword());
        //Create new account
        String insertAccountQuery = "INSERT INTO account(forename, surname, username, email_address, password, type, salt) VALUES (?,?, ?, ?, ?,  ?::account_type_enum, ?)";
        try {
            PreparedStatement st = connection.prepareStatement(insertAccountQuery);
            st.setString(1, crewMember.getForename());
            st.setString(2, crewMember.getSurname());
            st.setString(3, crewMember.getUsername());
            st.setString(4, crewMember.getEmailAddress());
            st.setString(5, passwords[0]);
            st.setString(6, crewMember.getAccountType().toString());
            st.setString(7, passwords[1]);
            st.executeUpdate();
        } catch (SQLException e) {
            System.err.println("In insertion of account " + e);
        }

        //Get the account's id so that it will be used for the foreign key of crew_member
        int accountId = 0;
        String getAccountId = "SELECT id FROM account WHERE username LIKE ?;";

        try {
            PreparedStatement st = connection.prepareStatement(getAccountId);
            st.setString(1, crewMember.getUsername());
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {

                    accountId = resultSet.getInt("id");
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

    public String getAllCrewMembers() throws SQLException {
        String query = """
                SELECT json_agg(jsonb_build_object('id', a.id, 'forename',
                a.forename, 'surname', a.surname, 'mail', a.email_address,'username', a.username, 'role', c.role, 'team'
                , c.team)) FROM shotmaniacs1.account a
                JOIN shotmaniacs1.crew_member c ON a.id = c.id
                WHERE a.type='crew_member'""";
        return getSQLString(query);
    }

    public String getProducers() throws SQLException {
        String query = """
                SELECT json_agg(json_build_object('id', a.id, 'forename',a.forename, 'surname', a.surname) ) 
                FROM account a, crew_member c 
                WHERE a.id = c.id AND c.role = 'producer'""";
        return getSQLString(query);

    }

    public void changeRole(int memberID, String newRole) throws SQLException {
        String getRole = "SELECT role FROM shotmaniacs1.crew_member WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(getRole);
        try {
            statement.setInt(1, memberID);
        } catch (SQLException e) {
            System.err.println(e);
        }

        ResultSet rs = statement.executeQuery();

        String crew_role = null;
        if (rs.next()) {
            crew_role = rs.getString(1);
        }

        String query = "UPDATE shotmaniacs1.crew_member " +
                "SET role = ?::role_enum " +
                "FROM shotmaniacs1.account " +
                "WHERE role = ?::role_enum AND crew_member.id = ? AND account.type = 'crew_member';";
        PreparedStatement st = connection.prepareStatement(query);
        try {
            st.setString(1, newRole);
            st.setString(2, crew_role);
            st.setInt(3, memberID);
            st.executeQuery();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void changeTeam(int id, String newTeam) throws SQLException {
        String getRole = "SELECT team FROM shotmaniacs1.crew_member WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(getRole);
        try {
            statement.setInt(1, id);
        } catch (SQLException e) {
            System.err.println(e);
        }

        ResultSet rs = statement.executeQuery();

        String crew_team = null;
        if (rs.next()) {
            crew_team = rs.getString(1);
        }

        String query = "UPDATE shotmaniacs1.crew_member " +
                "SET team = ?::team_enum " +
                "FROM shotmaniacs1.account " +
                "WHERE team = ?::team_enum AND crew_member.id = ? AND account.type = 'crew_member';";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, newTeam);
            st.setString(2, crew_team);
            st.setInt(3, id);
            st.executeQuery();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }


    /*METHODS RELATED TO ANNOUNCEMENTS*/
    public void addAnnouncement(AnnouncementBean announcement) throws SQLException {
        String insertQuery = "INSERT INTO announcement(announcer_id,title,body) VALUES (?,?,?)";
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1, announcement.getAnnouncer());
        st.setString(2, announcement.getTitle());
        st.setString(3, announcement.getBody());
        st.executeUpdate();
    }


    public String getAllAnnouncements() throws SQLException {
        String insertQuery = """
                SELECT json_agg(jsonb_build_object(
                    'announcement_id', subquery.id,
                    'announcement_title', subquery.title,
                    'announcement_body', subquery.body,
                    'announcement_timestamp', subquery.date_time,
                    'recipient', subquery.recipient,
                    'announcer', json_build_object(
                        'forename', subquery.forename,
                        'surname', subquery.surname
                    )
                )) AS result
                FROM (
                    SELECT ann.id, ann.title, ann.body, ann.date_time, a.forename, a.surname ,ann.recipient
                    FROM announcement ann
                    JOIN account a ON ann.announcer_id = a.id
                    ORDER BY ann.id DESC
                    LIMIT 10
                ) subquery;
                """;

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

    public String[] getEmailsOfEvent(int id) throws SQLException {
        String query = """
                   SELECT ARRAY_AGG(a.email_address) AS email_addresses
                             FROM account a, event e, event_enrollment ee
                             WHERE (
                               a.id = ee.crew_member_id AND
                               ee.event_id = e.id AND
                               e.id = ?
                             )
                   """;
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return (String[]) rs.getArray(1).getArray();
        }
        return null;
    }
}

