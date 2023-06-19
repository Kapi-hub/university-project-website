package dao;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import misc.Security;
import models.*;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static misc.Security.encodeSalt;


public enum AdminDao {
    I;
    final Connection connection;

    AdminDao() {
        connection = ConnectionFactory.getConnection();
    }

    public Response createNewMember(CrewMemberBean crewMember) {
        String insertCrewQuery =  "INSERT INTO crewMember(id, role, team) VALUES (?,?::Role_Enum,?::Team_Enum)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertCrewQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getRole().toString());
            st.setString(3, crewMember.getTeam().toString()) ;
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        String insertAccountQuery =  "INSERT INTO account(id, forename, surname, username, email_address, password, salt, type) VALUES (?,?, ?, ?, ? ?, ?, ?::AccountType)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertAccountQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getForename());
            st.setString(3, crewMember.getSurname()) ;
            st.setString(4, crewMember.getUsername()) ;
            st.setString(5, crewMember.getEmailAddress());

            String password = crewMember.getPassword();
            String[] encoded = Security.encodeSalt(password);
            st.setString(6,encoded[0]);
            st.setString(7,encoded[1]);
            st.setString(8, AccountType.CREW_MEMBER.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
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
    public void addAnnouncement(AnnouncementBean announcement) {
        String insertQuery = "INSERT INTO announcement(announcer_id,title,body) VALUES (?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1, announcement.getAnnouncer());
            st.setString(2, announcement.getTitle());
            st.setString(3, announcement.getBody());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public ArrayList<EventBean> getNotFullEvents() throws SQLException {
        String insertQuery = "SELECT e.id, e.name" +
                "FROM event e" +
                "JOIN event_requirement er ON e.id = er.event_id" +
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

    public ArrayList<AnnouncementBean> getAllAnnouncements() {
        String insertQuery = "SELECT * FROM announcement";
        ArrayList<AnnouncementBean> announcements = null;
        try {
            announcements = new ArrayList<>();
            PreparedStatement st = connection.prepareStatement(insertQuery);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                AnnouncementBean ab = new AnnouncementBean(rs.getInt("id"), rs.getInt("announcerid"), rs.getString("title"), rs.getString("body"), rs.getTimestamp("date_time"));
                announcements.add(ab);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return announcements;
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

