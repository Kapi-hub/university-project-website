package resources;

import dao.AdminDao;
import dao.ClientDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

import java.sql.SQLException;
import java.util.List;

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
    @Path("/crewAssignments")
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

    @PUT
    @Path("/crewAssignments/events?id=smth") //TODO change paths
    @RolesAllowed("admin")
    public void handleChangeEvent(EventBean event) {
        try{
            AdminDao.I.changeEventDetails(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DELETE
    @Path("/crewAssignments/deleteEvent/{eventID}")
    @RolesAllowed("admin")
    public Response handleDeleteEvent(@PathParam("eventID") int id) {
        try {
            AdminDao.I.deleteEvent(id);
            return Response.ok().build();
        }  catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError().build();
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
    @Path("/crewAssignments/smth")
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

//    //TODO: test with postman
//    @PUT
//    @Path("/crewAssignments/Member") //TODO change the url with the member id
//    @RolesAllowed("admin")
//    public void changeRole(CrewMemberBean crewMember, String newRole) {
//        try {
//            AdminDao.I.changeRole(crewMember, newRole);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //TODO: test with postman
//    @PUT
//    @Path("/crewAssignments/Member") //TODO change the url with the member id
//    @RolesAllowed("admin")
//    public void changeTeam(CrewMemberBean crewMember, String newTeam) {
//        try {
//            AdminDao.I.changeTeam(crewMember, newTeam);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
