package resources;

import dao.AdminDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;
import models.AnnouncementBean;
import models.CrewMemberBean;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dao.MailService.MAIL;

@Path("/admin")
public class AdminResource {

    @GET
    @Path("/user")
    @RolesAllowed("admin")
    public String getUser(@CookieParam("accountId") String accountIdString){
        int accountId = Integer.parseInt(accountIdString);
        try {
            return AdminDao.I.getUser(accountId);
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    /* METHODS RELATED TO ANNOUNCEMENTS */
    @POST
    @Path("/newAnnouncement")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response handlePostAnnouncement(AnnouncementBean announcement, @CookieParam("accountId") String accountIdString) {
        try {
            int accountId = Integer.parseInt(accountIdString);
            announcement.setAnnouncer(accountId);
            AdminDao.I.addAnnouncement(announcement);
            return Response.ok().build();
        } catch (SQLException e){
            return Response.serverError().build();
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/announcements")
    @RolesAllowed("admin")
    public Response getAnnouncement() {
        try {
            return Response.ok(AdminDao.I.getAllAnnouncements()).build();
        }catch (SQLException e){
            System.err.println(e.getMessage());
            return Response.serverError().build();
        }
    }


    /* METHODS RELATED TO EVENTS */
    @POST
    @Path("/crewAssignments/newEvent")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response handleCreateNewEvent(FormBean form) {
        try {
            int client_id = AdminDao.I.addClient(form.getClientBean());
            form.getEventBean().setClient_id(client_id);
            int event_id = AdminDao.I.addEvent(form.getEventBean());
            for (RequiredCrewBean required : form.getRequiredCrewBeans()) {
                required.setEvent_id(event_id);
                AdminDao.I.addRequirement(required);
            }
            return Response.ok().build();
        } catch (Exception e) {
            Response.serverError().build();
        }
        return null;
    }

    @GET
    @Path("/crewAssignments/bookings")
    @RolesAllowed("admin")
    public Response getLatestEvent() {
        try {
            return Response.ok(AdminDao.I.getLatestEvent()).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.serverError().build();
        }

    }

    @GET
    @Path("/crewReq")
    @RolesAllowed("admin")
    public Response showEventsWithoutCrew() {
        try{
            return Response.ok(AdminDao.I.getNotFullEvents()).build();
        }catch (SQLException e){
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/crewAssignments/changeEvent/{eventId}")
    @RolesAllowed("admin")
    public Response handleGetEventWithId(@PathParam("eventId") int id) {
        try{
            return Response.ok(AdminDao.I.getEventWithId(id)).build();
        }catch (SQLException e){
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/crewAssignments/deleteEvent/{eventID}")
    @RolesAllowed("admin")
    public Response handleDeleteEvent(@PathParam("eventID") int id) {
        try {
            String[] recipients = AdminDao.I.getEmailsOfEvent(id);
            AdminDao.I.deleteEvent(id);
            if (recipients != null && recipients.length > 0) {
                sendDeleteEmail(id, recipients);
            }
            return Response.ok().build();
        }  catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError().build();
        }
    }

    private static void sendDeleteEmail(int id, String[] recipients) {
        String subject = "Event has been deleted you have been assigned to.";
        String body = String.format(
                """
                        Dear Crew member,

                        An event with id-%s has been deleted, you have been assigned to.

                        Please consult the dashboard for more information.

                        Sincerely,
                        The Shotmaniacs Team
                        """, id);
        try {
            for (String recipient: recipients) {
                MAIL.sendMessage(recipient, subject, body);
            }

        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            e.printStackTrace();
        }
    }

    /* METHODS RELATED TO CREW-MEMBERS */
    @POST
    @Path("/crewAssignments/newMember")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response handleCreateNewMember(CrewMemberBean crewMember) {
        try {
            AdminDao.I.createNewMember(crewMember);
            return Response.ok().build();
        } catch (SQLException | GeneralSecurityException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/crewAssignments/members")
    @RolesAllowed("admin")
    public Response getAllCrewMembers() {
        try {
            return Response.ok(AdminDao.I.getAllCrewMembers()).build();
        }  catch (SQLException e){
           return Response.serverError().build();
        }
    }

    @GET
    @Path("/crewAssignments/newEvent")
    @RolesAllowed("admin")
    public Response getProducers() {
        try {
            return Response.ok(AdminDao.I.getProducers()).build();
        }  catch (SQLException e){
           return Response.serverError().build();
        }
    }

    @PUT
    @Path("/crewAssignments/changeRole/{memberID}/{role}")
    @RolesAllowed("admin")
    public Response changeRole(@PathParam("memberID") int id, @PathParam("role") String newRole) {
        try {
            AdminDao.I.changeRole(id, newRole);
            return Response.ok().build();
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/crewAssignments/changeTeam/{memberID}/{team}")
    @RolesAllowed("admin")
    public Response changeTeam(@PathParam("memberID") int id, @PathParam("team") String newTeam) {
        try {
            AdminDao.I.changeTeam(id, newTeam);
            return Response.ok().build();
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

}
