package resources;

import dao.CrewMemberDao;
import dao.EventDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.EventBean;
import models.RoleType;

import java.sql.SQLException;

@Path("/event")
public class EventResource {

    @Path("/enroll-self")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("crew_member")
    public Response enrollSelf(EventBean event, @CookieParam("accountId") String accountIdString) {
        int accountId = Integer.parseInt(accountIdString);
        try {
            RoleType role = CrewMemberDao.I.getRole(accountId);
            int required = EventDao.instance.getRequiredCrewSize(role, event.getId());
            int currentEnrolled = EventDao.instance.getCurrentEnrolmentsForRole(role, event.getId());
            if (required < currentEnrolled) {
                EventDao.instance.addEnrolment(accountId, event.getId());
                return Response.ok().build();
            } else {
                return Response.notModified()
                        .build();
            }
        } catch (SQLException ignored) {
            // ignored as serverError is default anyway
        }
        return Response.serverError()
                .build();
    }
}
