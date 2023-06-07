package resources;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import models.AnnouncementBean;

@Path("/admin/dashboard")
public class AnnouncementResource {

    @POST
    public void handlePostAnnouncement(AnnouncementBean announcement) {

    }
}
