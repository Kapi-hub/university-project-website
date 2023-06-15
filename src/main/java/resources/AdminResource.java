package resources;

import dao.AdminDao;
import dao.ClientDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Path("/admin")
public class AdminResource {
    @POST
    @Path("/dashboard/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response handlePostAnnouncement(AnnouncementBean announcement) {
        try {
            AdminDao.I.addAnnouncement(announcement);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
        return Response.ok()
                .build();
    }


    @GET
    @Path("/dashboard/all")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "crew_member"})
    public AnnouncementBean[] getAnnouncement() {
        try {
            return AdminDao.I.getAllAnnouncements();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


    @GET
    @Path("/dashboard/crewReq")
    public List<EventBean> showEventsWithoutCrew() {
        List<EventBean> events = null;
        try {
            events = AdminDao.I.getNotFullEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
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


    @POST
    @Path("/crewEvents/newMember") //TODO change the url
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleCreateNewMember(CrewMemberBean crewMember) {
        try {
            AdminDao.I.createNewMember(crewMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("crewEvents/newEvent")
    @Consumes(MediaType.APPLICATION_JSON)
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
}
