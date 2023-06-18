package dao;

import misc.ConnectionFactory;
import models.*;

import java.sql.*;
import java.util.ArrayList;
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

    public void createNewEvent(EventBean event) {
        String insertEventQuery = "INSERT INTO event(id, name, description, start, duration, location, type) VALUES (?,?,?,?,?,?::event_type_enum)";
        try {
            PreparedStatement st = connection.prepareStatement(insertEventQuery);
            st.setInt(1, event.getId());
            st.setString(2, event.getName());
            st.setString(3, event.getDescription());
            st.setTimestamp(4, event.getStart());
            st.setString(5, event.getLocation());
            st.setString(6, event.getType().toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public List<CrewMemberBean> getAllCrewMembers() throws SQLException {
        String query = "SELECT a.id, a.forename, a.surname FROM shotmaniacs1.account a WHERE a.type='crew_member'";
        List<CrewMemberBean> result = new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(query);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                CrewMemberBean crewMember = new CrewMemberBean(rs.getInt("id"), rs.getString("forename"), rs.getString("surname"));
                result.add(crewMember);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
        String insertQuery = "INSERT INTO announcement(announcer_id,title,body) VALUES (?,?,?)";
        PreparedStatement st = connection.prepareStatement(insertQuery);
        st.setInt(1, announcement.getAnnouncer());
        st.setString(2, announcement.getTitle());
        st.setString(3, announcement.getBody());
        st.executeUpdate();
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
                WHERE ee.enrollments < er.crew_size OR ee.enrollments IS NULL; 
                """;

        ArrayList<EventBean> events = new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(insertQuery);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                EventBean eb = new EventBean(rs.getInt("id"), rs.getString("name"));
                events.add(eb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
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
}

