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


@Path("/admin")
public class AdminResource {
Connection connection = ConnectionFactory.getConnection();
    @POST
    @Path("/dashboard/new")
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
    @Path("/crewEvents/") //TODO change the url
    public Response handleCreateNewMember(CrewMemberBean crewMember) {
        String insertQuery =  "INSERT INTO crewMember(id, role, team) VALUES (?,?::Role,?::Team)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getRole().toString());
            st.setString(3, crewMember.getTeam().toString()) ;
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        /*TODO create query that adds the rest of the crewMember details too - maybe also change in the js to create 2
        different jsons - one for Id, role, team, and another one for the rest of the details*/
        return Response.accepted().build();
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
