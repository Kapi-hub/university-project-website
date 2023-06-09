package resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import models.AnnouncementBean;
import models.CrewMemberBean;
import models.EventBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Path("/admin/dashboard")
public class AdminResource {
Connection connection = ConnectionFactory.getConnection();
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handlePostAnnouncement(AnnouncementBean announcement) {
        String insertQuery =    "INSERT INTO announcements(announcer_id,title,body) VALUES (?,?,?)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1,announcement.getAnnouncer());
            st.setString(2,announcement.getTitle());
            st.setString(3, announcement.getBody()) ;
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return Response.accepted().build();
    }

    @POST
    public void handleCreateNewMember(CrewMemberBean crewMember) {

    }

    @POST
    public void handlerCreateNewEvent(EventBean event) {

    }

    @GET
    public void getAnnouncement(AnnouncementBean announcement) {

    }

    @GET
    public void getCrewMember(CrewMemberBean crewMember) {

    }

    @GET
    public void getEvent(EventBean event) {

    }
}
