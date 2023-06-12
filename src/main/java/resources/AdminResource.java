package resources;

import dao.AdminDao;
import dao.ClientDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import models.*;

import java.sql.*;
import java.util.ArrayList;


@Path("/admin")
public class AdminResource {
    @POST
    @Path("/dashboard/new")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handlePostAnnouncement(AnnouncementBean announcement) {
        String insertQuery = "INSERT INTO announcement(announcer_id,title,body) VALUES (?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1, announcement.getAnnouncer());
            st.setString(2, announcement.getTitle());
            st.setString(3, announcement.getBody());
            //TODO: add also the id of the announcement and the date and time
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return Response.accepted().build();
    }


    @GET
    @Path("/dashboard/all")
    public ArrayList<AnnouncementBean> getAnnouncement(AnnouncementBean announcement) {
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


    @GET
    @Path("/dashboard/crewRew")
    public void getCrewMember(CrewMemberBean crewMember) {
//        String insertQuery = "SELECT * FROM announcement"; //TODO how do you know when more crew is needed
//        ArrayList<AnnouncementBean> announcements =null;
//        try {
//            announcements = new ArrayList<>();
//            PreparedStatement st = connection.prepareStatement(insertQuery);
//            ResultSet rs = st.executeQuery();
//            while (rs.next()) {
//                AnnouncementBean ab = new AnnouncementBean(rs.getInt("id"), rs.getInt("announcerid"), rs.getString("title"), rs.getString("body"), rs.getTimestamp("date_time"));
//                announcements.add(ab);
//            }
//        } catch (SQLException e) {
//            System.out.println(e);
//        }
//        return announcements;
//    }
    }

    @GET
    @Path("/dashboard/latest")
    public ArrayList<EventBean> getEvent(EventBean event) {
        String insertQuery = "SELECT name, description,start,duration,location,type FROM event  ORDER BY id DESC";
        ArrayList<EventBean> events = null;
        try {
            events = new ArrayList<>();
            PreparedStatement st = connection.prepareStatement(insertQuery);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                try {
                    EventType type = EventType.valueOf(rs.getString("type"));
                    EventBean eventBean = new EventBean(
                            rs.getString("name"), rs.getString("description"),
                            rs.getTimestamp("start"), rs.getInt("duration"),
                            rs.getString("location"), type);
                    events.add(eventBean);
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return events;
    }


    @POST
    @Path("/crewEvents/") //TODO change the url
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleCreateNewMember(CrewMemberBean crewMember) {
        try {
            AdminDao.I.createNewMember(crewMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("crewEvents/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleCreateNewEvent(FormBean form) throws SQLException {
        try {
            int client_id = ClientDao.I.addClient(form.getClientBean());
            formBean.getEventBean().setClient_id(client_id);
            int event_id = ClientDao.I.addEvent(form.getEventBean());
            for (RequiredCrewBean required : form.getRequiredCrewBeans()) {
                required.setEvent_id(event_id);
                ClientDao.I.addRequirement(required);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
