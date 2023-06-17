package resources;

import dao.AdminDao;
import dao.ClientDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import models.*;

import java.sql.SQLException;
import java.util.ArrayList;
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
    @Path("/dashboard/all")
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
    @Path("/crewEvents/newEvent")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void handleCreateNewEvent(FormBean form) throws SQLException {
        try {
            int client_id = ClientDao.I.addClient(form.getClientBean());
            form.getEventBean().setClient_id(client_id);
            int event_id = ClientDao.I.addEvent(form.getEventBean());
            for (RequiredCrewBean required : form.getRequiredCrewBeans()) {
                required.setEvent_id(event_id);
                ClientDao.I.addRequirement(required);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/dashboard/latest")
    public ArrayList<EventBean> getLatestEvent() {
        ArrayList<EventBean> events = null;
        try {
            events = AdminDao.I.getLatestEvent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    @GET
    @Path("/dashboard/crewReq")
    public List<EventBean> showEventsWithoutCrew() {
        List<EventBean> events = null;
        try{
            events = AdminDao.I.getNotFullEvents();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return events;
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

    @GET
    @Path("/crewAssignments")
    @RolesAllowed("admin")
    public List<CrewMemberBean> getAllCrewMembers() {
        try {
            return AdminDao.I.getAllCrewMembers();
        }  catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
