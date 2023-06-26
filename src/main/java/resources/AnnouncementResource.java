package resources;

import dao.AnnouncementDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.AnnouncementResponseBean;

import java.sql.SQLException;

@Path("/announcement")
public class AnnouncementResource {

    @Path("/forCrew")
    @GET
    @RolesAllowed("crew_member")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnouncementsForCrew(@CookieParam("accountId") String accountIdString) {
        int accountId = Integer.parseInt(accountIdString);
        AnnouncementResponseBean[] announcements;
        try {
            announcements = AnnouncementDao.instance.getAnnouncementsForCrew(accountId);
        } catch (SQLException e) {
            System.err.println("Error getting announcements for crew member " + accountId + ": " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if (announcements == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(announcements).build();
    }
}
