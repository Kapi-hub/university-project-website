package dao;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public enum AdminDao {
    I;
    final Connection connection;

    AdminDao() {
        connection = ConnectionFactory.getConnection();
    }

    public Response createNewMember(CrewMemberBean crewMember) {
        String insertCrewQuery =  "INSERT INTO crewMember(id, role, team) VALUES (?,?::Role_enum,?::Team_enum)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertCrewQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getRole().toString());
            st.setString(3, crewMember.getTeam().toString()) ;
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        String insertAccountQuery =  "INSERT INTO account(id, forename, surname, username, email_address, password, type) VALUES (?,?, ?, ?, ? ?, ?::AccountType)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertAccountQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getForename());
            st.setString(3, crewMember.getSurname()) ;
            st.setString(4, crewMember.getUsername()) ;
            st.setString(5, crewMember.getEmailAddress());
            st.setString(6, crewMember.getPassword());
            st.setString(7, AccountType.CREW_MEMBER.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        //TODO handle responses
        return Response.accepted().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createNewEvent(EventBean event) {
        String insertEventQuery =  "INSERT INTO event(id, name, description, start, duration, location, type) VALUES (?,?,?,?,?,?::event_type_enum)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertEventQuery);
            st.setInt(1, event.getId());
            st.setString(2,event.getName());
            st.setString(3, event.getDescription()) ;
            st.setTimestamp(4, event.getStart()); ;
            st.setString(5, event.getLocation()) ;
            st.setString(6,event.getType().toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addRequirement(List<RequiredCrewBean> required) throws SQLException {
        for(RequiredCrewBean requiredCrewBean:required) {
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
        String insertQuery = "SELECT e.id, e.name " +
                "FROM event e " +
                "JOIN event_requirement er ON e.id = er.event_id " +
                "LEFT JOIN (" +
                "  SELECT event_id, COUNT(*) AS enrollments" +
                "  FROM event_enrollment" +
                "  GROUP BY event_id" +
                ") ee ON e.id = ee.event_id" +
                "WHERE ee.enrollments < er.crew_size OR ee.enrollments IS NULL; ";

        ArrayList<EventBean> events =new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(insertQuery);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                EventBean eb = new EventBean(rs.getInt("id"), rs.getString("name"));
                events.add(eb);
            }
        }catch (Exception e){
        e.printStackTrace();
        }
       return events;
    }

    public String getAllAnnouncements() throws SQLException {
        String insertQuery = """
                SELECT json_agg(jsonb_build_object(
                           'announcement_id', ann.id,
                           'announcement_title', ann.title,
                           'announcement_body', ann.body,
                           'announcement_timestamp', ann.date_time,
                           'announcer', json_build_object(                         
                                            'forename', a.forename,
                                            'surname', a.surname
                                        )
                           )
                ) AS result
                FROM announcement ann
                JOIN account a ON ann.announcer_id = a.id;
                """;

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
        return null; // Return null if no result is found
    }

    public ArrayList<EventBean> getLatestEvent() throws SQLException {
        String insertQuery = "SELECT name, description,start,duration,location,type FROM event  ORDER BY id DESC";
        ArrayList<EventBean> events = null;
            events = new ArrayList<>();
            PreparedStatement st = connection.prepareStatement(insertQuery);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                    EventType type = EventType.valueOf(rs.getString("type"));
                    EventBean eventBean = new EventBean(
                            rs.getString("name"), rs.getString("description"),
                            rs.getTimestamp("start"), rs.getInt("duration"),
                            rs.getString("location"), type);
                    events.add(eventBean);
                }
            return events;
        }
}

