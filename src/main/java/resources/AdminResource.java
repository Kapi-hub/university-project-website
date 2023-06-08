package resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import models.AnnouncementBean;
import models.CrewMemberBean;
import models.EventBean;

@Path("/admin/dashboard")
public class AdminResource {

    @POST
    public void handlePostAnnouncement(AnnouncementBean announcement) {

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
