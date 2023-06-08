package resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import models.AnnouncementBean;

@Path("/admin/dashboard")
public class AnnouncementResource {

    @Path("/new")
    @POST
    @Consumes
    public void handlePostAnnouncement(AnnouncementBean announcement) {

    }
}
