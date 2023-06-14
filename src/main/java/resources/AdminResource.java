package resources;

import dao.AdminDao;
import dao.ClientDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import models.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Path("/admin")
public class AdminResource {
    @POST
    @Path("/dashboard/new")
    @Consumes(MediaType.APPLICATION_JSON)
    public void handlePostAnnouncement(AnnouncementBean announcement) {
        try {
            AdminDao.I.addAnnouncement(announcement);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @GET
    @Path("/dashboard/all")
    public ArrayList<AnnouncementBean> getAnnouncement(AnnouncementBean announcement) {
        ArrayList<AnnouncementBean> announcements = new ArrayList<>();
        try {
            announcements =  AdminDao.I.getAllAnnouncements();
        }catch (Exception e){
            e.printStackTrace();
        }
        return announcements;
    }


    @GET
    @Path("/dashboard/crewReq")
    public List<EventBean> showEventsWithoutCrew() {
        List<EventBean> events = null;
        try{
            events = AdminDao.I.getNotFullEvents();
        }catch (Exception e){
            e.printStackTrace();
        }
        return events;
    }

    @GET
    @Path("/dashboard/latest")
    public ArrayList<EventBean> getLatestEvent(EventBean event) {
        ArrayList<EventBean> events = null;
        try {
            events = AdminDao.I.getLatestEvent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
