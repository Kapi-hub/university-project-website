package resources;

import dao.AdminDao;
import dao.ClientDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dao.MailService.MAIL;

@Path("/admin")
public class AdminResource {

    /* METHODS RELATED TO ANNOUNCEMENTS */
    @POST
    @Path("/dashboard/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void handlePostAnnouncement(AnnouncementBean announcement, @CookieParam("accountId") String accountIdString) {
        try {
            int accountId = Integer.parseInt(accountIdString);
            announcement.setAnnouncer(accountId);
            AdminDao.I.addAnnouncement(announcement);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/announcements")
    @RolesAllowed("admin")
    public String getAnnouncement() {
        try {
            return AdminDao.I.getAllAnnouncements();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    /* METHODS RELATED TO EVENTS */
    @POST
    @Path("/crewAssignments/newEvent")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void handleCreateNewEvent(FormBean form) throws SQLException {
        try {
            int client_id = AdminDao.I.addClient(form.getClientBean());
            form.getEventBean().setClient_id(client_id);
            int event_id = AdminDao.I.addEvent(form.getEventBean());
            for (RequiredCrewBean required : form.getRequiredCrewBeans()) {
                required.setEvent_id(event_id);
                AdminDao.I.addRequirement(required);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/crewAssignments/bookings")
    @RolesAllowed("admin")
    public String getLatestEvent() {
        try {
            return AdminDao.I.getLatestEvent();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    @GET
    @Path("/crewReq")
    @RolesAllowed("admin")
    public List<EventBean> showEventsWithoutCrew() {
        List<EventBean> events = null;
        try{
            events = AdminDao.I.getNotFullEvents();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return events;
    }

    @GET
    @Path("/crewAssignments/changeEvent/{eventId}")
    @RolesAllowed("admin")
    public String handleGetEventWithId(@PathParam("eventId") int id) {
        try{
            return AdminDao.I.getEventWithId(id);
        }catch (SQLException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

//    @PUT
//    @Path("/crewAssignments/changeEvent/{eventId}")
//    @RolesAllowed("admin")
//    public void handleChangeEvent(@PathParam("eventId") int id) {
//        try{
//            AdminDao.I.changeEventDetails(id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
    public void handleCreateNewMember(CrewMemberBean crewMember) {
        try {
            AdminDao.I.createNewMember(crewMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/crewAssignments/members")
    @RolesAllowed("admin")
    public String getAllCrewMembers() {
        try {
            return AdminDao.I.getAllCrewMembers();
        }  catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/crewAssignments/newEvent")
    @RolesAllowed("admin")
    public String getProducers() {
        try {
            return AdminDao.I.getProducers();
        }  catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @PUT
    @Path("/crewAssignments/changeRole/{memberID}/{role}") //TODO change the url with the member id
    @RolesAllowed("admin")
    public void changeRole(@PathParam("memberID") int id, @PathParam("role") String newRole) {
        try {
            AdminDao.I.changeRole(id, newRole);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PUT
    @Path("/crewAssignments/changeTeam/{memberID}/{team}")
    @RolesAllowed("admin")
    public void changeTeam(@PathParam("memberID") int id, @PathParam("team") String newTeam) {
        try {
            AdminDao.I.changeTeam(id, newTeam);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
