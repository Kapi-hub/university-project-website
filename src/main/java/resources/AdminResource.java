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
    /**
     * @param accountIdString the user's account id
     * @return the user id based on the cookie
     */
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

    /**
     * This method handles the creation of a new event based on a FormBean object.
     * @param form - object of type Formbean that contains an object of type EventBean and another one of type ClientBean
     * @return a response 200 if the creation of a new event is successful, of 500 if something fails
     */
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
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    /**
     * This method fetches all the events from the database
     * @return a response 200 if the fetch of all events is successful, of 500 if something fails
     */
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

    /**
     * This method fetches all the events that do not have all the crew needed assigned.
     * @return a response 200 if the fetching of events without all crew assigned is successful,
     * or 500 if something fails
     */
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

    /**
     *
     * This method fetches all event details based on its id
     * @param id - event id
     * @return a response 200 if the fetching of the event is successful, or 500 if something fails
     */
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

    /**
     * This method deletes an event based on its id.
     * @param id - event id
     * @return a response 200 if the deletion of an event is successful, or 500 if something fails
     */
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

    /**
     * This method creates a new crew member based on a CrewMemberBean object.
     * @param crewMember - object that contains all attributes
     * @return a response 200 if the creation of a new crew member is successful, or 500 if something fails
     */
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

    /**
     * This method fetches all members from the database.
     * @return a response 200 if the fetch of crew members is successful, or 500 if something fails
     */
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

    /**
     * This method fetches all members that have the "producer" role
     * @return a response 200 if the fetch of producers is successful, or 500 if something fails
     */
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

    /**
     * This method changes the role of a given crew member with a new role
     * @param id - crew member's id
     * @param newRole - crew member's new role
     * @return a response 200 if the change of role is successful, or 500 if something fails
     */
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

    /**
     * This method changes the team of a given crew member with a new team
     * @param id - crew member's id
     * @param newTeam - crew member's new team
     * @return a response 200 if the change of team is successful, or 500 if something fails
     */
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
