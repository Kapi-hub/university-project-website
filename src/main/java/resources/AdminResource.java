package resources;

import dao.AdminDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import models.AnnouncementBean;
import models.CrewMemberBean;

import java.sql.SQLException;

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
    public void handlePostAnnouncement(AnnouncementBean announcement, @CookieParam("accountId") String accountIdString) {
        try {
            int accountId = Integer.parseInt(accountIdString);
            announcement.setAnnouncer(accountId);
            AdminDao.I.addAnnouncement(announcement);
        }catch (SQLException e){
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
            return null;
        }
    }


    /* METHODS RELATED TO EVENTS */
//    @POST
//    @Path("/crewAssignments")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("admin")
//    public void handleCreateNewEvent(FormBean form) throws SQLException {
//        try {
//            int client_id = AdminDao.I.addClient(form.getClientBean());
//            form.getEventBean().setClient_id(client_id);
//            int event_id = AdminDao.I.addEvent(form.getEventBean());
//            for (RequiredCrewBean required : form.getRequiredCrewBeans()) {
//                required.setEvent_id(event_id);
//                AdminDao.I.addRequirement(required);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @GET
    @Path("/events")
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
    public String showEventsWithoutCrew() {
        try{
            return AdminDao.I.getNotFullEvents();
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    /* METHODS RELATED TO CREW-MEMBERS */
    @POST
    @Path("/crewAssignments")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void handleCreateNewMember(CrewMemberBean crewMember) {
        try {
            AdminDao.I.createNewMember(crewMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @GET
//    @Path("/crewAssignments")
//    @RolesAllowed("admin")
//    public String getAllCrewMembers() {
//        try {
//            return AdminDao.I.getAllCrewMembers();
//        }  catch (SQLException e){
//            e.printStackTrace();
//            return null;
//        }
//    }

    @GET
    @Path("/crewAssignments")
    @RolesAllowed("admin")
    public String getProducers() {
        try {
            return AdminDao.I.getProducers();
        }  catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
