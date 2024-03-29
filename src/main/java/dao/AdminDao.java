package dao;

import misc.ConnectionFactory;
import models.*;

import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.List;

import static misc.Security.encodeSalt;


public enum AdminDao {
    I;
    final Connection connection;

    /**
     * Sets up connection with singleton pattern
     */
    AdminDao() {
        connection = ConnectionFactory.getConnection();
    }
    /*METHODS RELATED TO EVENTS*/

    /**
     * Adds a client to the database
     * @param client the client it will add
     * @return the client_id
     * @throws SQLException error when working with SQL
     */

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
        query = "INSERT INTO client (id, phone_number) VALUES (?, ?)";
        st = connection.prepareStatement(query);
        st.setInt(1, client_id);
        st.setString(2, client.getPhone_number());
        st.executeUpdate();
        return client_id;
    }

    /**
     * This method inserts a new event in the database
     * @param event - the event to be inserted
     * @return - the new event's id
     * @throws SQLException
     */
    public int addEvent(EventBean event) throws SQLException {
        String query = "INSERT INTO event (client_id, name, description, start, duration, location, production_manager_id, type, booking_type) VALUES (?,?,?,?,?,?, ?, ?::event_type_enum, ?::booking_type_enum) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, event.getClient_id());
        st.setString(2, event.getName());
        st.setString(3, event.getDescription());
        st.setTimestamp(4, event.getStart());
        st.setInt(5, event.getDuration());
        st.setString(6, event.getLocation());
        st.setInt(7, event.getProduction_manager_id());
        st.setString(8, event.getType().toString());
        st.setString(9, event.getBooking_type().toString());


        ResultSet rs = st.executeQuery();
        int event_id = -1;
        if (rs.next())
            event_id = rs.getInt(1);
        System.out.println(rs);
        return event_id;
    }

    /**
     * This method inserts in the database the required crews needed for each new event
     * @param required
     * @throws SQLException
     */
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


    public void addRequirement(List<RequiredCrewBean> required) throws SQLException {
        for (RequiredCrewBean requiredCrewBean : required) {
//            String query = "INSERT INTO event_requirement (event_id, crew_size, role) VALUES (?, ?, ?::role_enum)";
            PreparedStatement st = connection.prepareStatement("SELECT insert_requirements(?, ?, ?::role_enum)" );
            st.setInt(1, requiredCrewBean.getEvent_id());
            st.setInt(2, requiredCrewBean.getCrew_size());
            st.setString(3, requiredCrewBean.getRole().toString());
            st.executeUpdate();
        }
    }


    public void addAnnouncement(AnnouncementBean announcement) throws SQLException {
        if (announcement.getRecipient() == 0 && !announcement.isUrgent()){
            String insertQuery = "INSERT INTO announcement(announcer_id,title,body) VALUES (?,?,?)";
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1, announcement.getAnnouncer());
            st.setString(2, announcement.getTitle());
            st.setString(3, announcement.getBody());
            st.executeUpdate();

        }
        else if (announcement.getRecipient() != 0 && !announcement.isUrgent()){
        String insertQuery = "INSERT INTO announcement(announcer_id,title,body,recipient) VALUES (?,?,?,?)";
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1, announcement.getAnnouncer());
        st.setString(2, announcement.getTitle());
        st.setString(3, announcement.getBody());
        st.setInt(4, announcement.getRecipient());
        st.executeUpdate();
        }
        else if (announcement.getRecipient() == 0 && announcement.isUrgent()){
            String insertQuery = "INSERT INTO announcement(announcer_id,title,body,urgent) VALUES (?,?,?,true)";
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1, announcement.getAnnouncer());
            st.setString(2, announcement.getTitle());
            st.setString(3, announcement.getBody());
            st.executeUpdate();
        }
        else {
            String insertQuery = "INSERT INTO announcement(announcer_id,title,body,recipient,urgent) VALUES (?,?,?,?,true)";
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1, announcement.getAnnouncer());
            st.setString(2, announcement.getTitle());
            st.setString(3, announcement.getBody());
            st.setInt(4, announcement.getRecipient());
            st.executeUpdate();
        }
    }

    /**
     * This method fetches the events that need crew to be assigned
     * @return a String with all the results
     * @throws SQLException
     */
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
                WHERE (ee.enrollments < (
                    SELECT SUM(crew_size)
                    FROM event_requirement
                    WHERE event_id = e.id
                    GROUP BY ee.event_id
                ) OR ee.enrollments IS NULL)
                  AND e.id IS NOT NULL
                  AND e.name IS NOT NULL
                  AND e.status != 'to do';
                        """;
        return getSQLString(insertQuery);
    }


    /**
     * This method returns all the event details based on an id
     * @param id - event id
     * @return a string with all the details in a JSON form
     * @throws SQLException
     */
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
                "FROM event " +
                "WHERE id = ?";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, id);
        return getSQLString(st.toString());
    }


    public void changeEventDetails(EventBean event) throws SQLException {
        String updateQuery = "UPDATE event SET name=?, description=?, start=?, duration=?, location=?, type=?, " +
                "booking_type=? WHERE id=?";
        PreparedStatement st = connection.prepareStatement(updateQuery);
        st.setString(1, event.getName());
        st.setString(2, event.getDescription());
        st.setTimestamp(3, event.getStart());
        st.setInt(4, event.getDuration());
        st.setString(5, event.getLocation());
        st.setString(6, event.getType().toString());
        st.setString(7, event.getBooking_type().toString());
        st.setInt(8, event.getId());
    }

    /**
     * This method deletes an event based on the id
     * @param id - the event's id
     * @throws SQLException
     */
    public void deleteEvent(int id) throws SQLException {
        String query = """
                DELETE FROM event" +
                "WHERE id = ?""";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, id);

        st.executeUpdate();
    }

    /*METHODS RELATED TO CREW MEMBERS*/

    /**
     * This method inserts a new crew member into the database.
     * @param crewMember
     * @throws GeneralSecurityException
     * @throws SQLException
     */
    public void createNewMember(CrewMemberBean crewMember) throws GeneralSecurityException, SQLException {
        String[] passwords = encodeSalt(crewMember.getPassword());
        //Create new account
        String insertAccountQuery = "INSERT INTO account(forename, surname, username, email_address, password, type, salt) VALUES (?,?, ?, ?, ?,  ?::account_type_enum, ?)";

        PreparedStatement st = connection.prepareStatement(insertAccountQuery);
        st.setString(1, crewMember.getForename());
        st.setString(2, crewMember.getSurname());
        st.setString(3, crewMember.getUsername());
        st.setString(4, crewMember.getEmailAddress());
        st.setString(5, passwords[0]);
        st.setString(6, crewMember.getAccountType().toString());
        st.setString(7, passwords[1]);
        st.executeUpdate();

        //Get the account's id so that it will be used for the foreign key of crew_member
        int accountId = 0;
        String getAccountId = "SELECT id FROM account WHERE username LIKE ?;";

        st = connection.prepareStatement(getAccountId);
        st.setString(1, crewMember.getUsername());
        try (ResultSet resultSet = st.executeQuery()) {
            if (resultSet.next()) {

                accountId = resultSet.getInt("id");
            }
        }

        //Create crew_member
        String insertCrewQuery = "INSERT INTO crew_member(id, role, team) VALUES(?, ?::role_enum, ?::team_enum)";

        st = connection.prepareStatement(insertCrewQuery);
        st.setInt(1, accountId);
        st.setString(2, crewMember.getRole().toString());
        st.setString(3, crewMember.getTeam().toString());
        st.executeUpdate();

    }
    /**
     * This method fetches all the crew members that are in the database.
     */
    public String getAllCrewMembers() throws SQLException {
        String query = """
                SELECT json_agg(jsonb_build_object('id', a.id, 'forename',
                a.forename, 'surname', a.surname, 'mail', a.email_address,'username', a.username, 'role', c.role, 'team'
                , c.team)) FROM account a
                JOIN crew_member c ON a.id = c.id
                WHERE a.type='crew_member'""";
        return getSQLString(query);
    }

    /**
     * This method fetches all the producers from the database
     */
    public String getProducers() throws SQLException {
        String query = """
                SELECT json_agg(json_build_object('id', a.id, 'forename',a.forename, 'surname', a.surname) ) 
                FROM account a, crew_member c 
                WHERE a.id = c.id AND c.role = 'producer'""";
        return getSQLString(query);

    }

    /**
     * This method changes the role of a crew member.
     */
    public void changeRole(int memberID, String newRole) throws SQLException {
        String getRole = "SELECT role FROM crew_member WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(getRole);

        statement.setInt(1, memberID);


        ResultSet rs = statement.executeQuery();

        String crew_role = null;
        if (rs.next()) {
            crew_role = rs.getString(1);
        }

        String query = "UPDATE crew_member " +
                "SET role = ?::role_enum " +
                "FROM account " +
                "WHERE role = ?::role_enum AND crew_member.id = ? AND account.type = 'crew_member';";
        PreparedStatement st = connection.prepareStatement(query);

        st.setString(1, newRole);
        st.setString(2, crew_role);
        st.setInt(3, memberID);
        st.executeUpdate();
    }

    /**
     * This method changes the team of a crew member
     */
    public void changeTeam(int id, String newTeam) throws SQLException {
        String getRole = "SELECT team FROM crew_member WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(getRole);

        statement.setInt(1, id);


        ResultSet rs = statement.executeQuery();

        String crew_team = null;
        if (rs.next()) {
            crew_team = rs.getString(1);
        }

        String query = "UPDATE crew_member " +
                "SET team = ?::team_enum " +
                "FROM account " +
                "WHERE team = ?::team_enum AND crew_member.id = ? AND account.type = 'crew_member';";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, newTeam);
        st.setString(2, crew_team);
        st.setInt(3, id);
        st.executeUpdate();

    }





    /**
     * Returns all announcements in JSON form
     * @return announcencements in JSON format
     * @throws SQLException sql error
     */
    public String getAllAnnouncements() throws SQLException {
        String insertQuery = """
                SELECT json_agg(jsonb_build_object(
                    'announcement_id', subquery.id,
                    'announcement_title', subquery.title,
                    'announcement_body', subquery.body,
                    'announcement_timestamp', subquery.date_time,
                    'recipient', subquery.recipient,
                    'urgency',subquery.urgent,
                    'announcer', json_build_object(
                        'forename', subquery.forename,
                        'surname', subquery.surname
                    )
                )) AS result
                FROM (
                    SELECT ann.id, ann.title, ann.body, ann.date_time, ann.urgent, a.forename, a.surname ,ann.recipient
                    FROM announcement ann
                    JOIN account a ON ann.announcer_id = a.id
                    ORDER BY ann.id DESC
                ) subquery;
                """;

        return getSQLString(insertQuery);
    }

    /**
     * This method fetches all the events from the database
     * @return a String with all the events put in a JSON
     * @throws SQLException
     */
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
                                       'production_manager_id', e.production_manager_id,
                                       'type', e.type,
                                       'booking_type', e.booking_type,
                                       'clients', (SELECT json_agg(
                                                                  json_build_object(
                                                                          'forename', a.forename,
                                                                          'surname', a.surname,
                                                                          'emailAddress', a.email_address,
                                                                          'phone_number', c.phone_number
                                                                      ))
                                                   FROM account a
                                                            JOIN client c ON a.id = c.id
                                                   WHERE a.type = 'client'
                                                     AND c.id = e.client_id),
                                       'requirements',
                                       (SELECT json_agg(json_build_object('role', role, 'crew_size', crew_size)) AS json_data
                                        FROM event_requirement r
                                        WHERE e.id = r.event_id
                                        GROUP BY r.event_id)
                ))
                           ) AS result
                FROM event e
                 """;
        return getSQLString(insertQuery);
    }

    public String getIncomingEvents() throws SQLException {
        String insertQuery = """
                SELECT json_agg(json_build_object(
                        'name',name,
                        'id',id,
                        'description',description,
                        'start',start,
                        'duration',duration,
                        'type',type
                    )
                           )AS result
                From event
                WHERE status = 'to do'
                    """;//TODO make it show latest as group by order by won't give one row
        return getSQLString(insertQuery);
    }

    /**
     * Gets the first string result
     * @param insertQuery the query it will handle
     * @return the first string of the first row
     * @throws SQLException sql error
     */
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

    /**
     * Gets the forename based of accountID
     * @param accountId the accountID
     * @return the forename
     * @throws SQLException sql error
     */
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

    /**
     * Returns a list of all emails of enrolled crew members
     * @param id the id of the event
     * @return the email list it returns
     * @throws SQLException sql error
     */
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
        Array arr;
        if (rs.next() && (arr=rs.getArray(1)) != null) {
            return (String[]) arr.getArray();
        }
        return null;
    }

    public String getEvent(int id) throws SQLException {
        String insertQuery = """
               SELECT JSON_BUILD_OBJECT(
               'eventTitle', name,
               'eventDescription', description,
               'eventDateTime', start,
               'eventDuration', duration,
               'eventLocation', location,
               'eventType', type::text,
               'bookingType', booking_type::text
           ) AS json_data
            FROM shotmaniacs1.event
            WHERE id = ?;
                """;
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        if (rs.next()){
            return rs.getString(1);
        }
        return null;
    }

    public String getCrewReq(int id) throws SQLException {
        String insertQuery = """
                SELECT JSON_AGG(json_data) AS json_result 
                FROM (
                       SELECT
                       JSON_BUILD_OBJECT(
                               'videographer', SUM(CASE WHEN role = 'videographer' THEN crew_size ELSE 0 END),
                               'assistant', SUM(CASE WHEN role = 'assistant' THEN crew_size ELSE 0 END),
                               'photographer', SUM(CASE WHEN role = 'photographer' THEN crew_size ELSE 0 END),
                               'producer', SUM(CASE WHEN role = 'producer' THEN crew_size ELSE 0 END),
                               'planner', SUM(CASE WHEN role = 'planner' THEN crew_size ELSE 0 END),
                               'data_handler', SUM(CASE WHEN role = 'data_handler' THEN crew_size ELSE 0 END),
                               'editor', SUM(CASE WHEN role = 'editor' THEN crew_size ELSE 0 END)
                       ) AS json_data
                       FROM event_requirement
                       WHERE event_id = ?
                   ) AS subquery;
                """;
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        if (rs.next()){
            return rs.getString(1);
        }
        return null;
    }

    public String getEnrolledCrew(int id) throws SQLException {
        String insertQuery= """
        SELECT JSON_AGG(json_data)
        FROM (
                SELECT JSON_BUILD_OBJECT(
                        'id', crew_member.id,
                        'name', forename,
                        'lastName', surname,
                        'role', crew_member.role
                ) AS json_data
                FROM shotmaniacs1.crew_member
                INNER JOIN event_enrollment ON crew_member.id = event_enrollment.crew_member_id
                JOIN account ON account.id = crew_member.id
                WHERE account.id = crew_member.id
                AND event_id = ?
        ) AS subquery
        """;
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return rs.getString(1);
        return null;
    }

    public void toInProgress(int id) throws SQLException {
        String insertQuery = """
                UPDATE event
                set status ='review'
                WHERE id = ?
                """;
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1,id);
        st.executeUpdate();

    }
}

