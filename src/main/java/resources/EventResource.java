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
import models.*;

import java.sql.SQLException;

@Path("/event")
public class EventResource {

    /**
     * In JSON, use syntax:
     * "{
     * "id": *the event id*
     * }"
     */
    @Path("/enroll-self")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("crew_member")
    public Response enrollSelf(EventBean event, @CookieParam("accountId") String accountIdString) {
        int accountId = Integer.parseInt(accountIdString);
        return enrol(accountId, event.getId());
    }

    /**
     * In JSON, use syntax:
     * "{
     * "crewMember": {
     * "id": *the crewMember's account id*
     * },
     * "event": {
     * "id": *the event id*
     * }
     * }"
     */
    @Path("/force-enroll")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response forceEnrolCrew(ForceEnrolBean beans) {
        CrewMemberBean crewMemberBean = beans.getCrewMember();
        EventBean eventBean = beans.getEvent();
        int crewMemberId = crewMemberBean.getId();
        int eventId = eventBean.getId();
        return enrol(crewMemberId, eventId);
    }

    /**
     * "{
     * "eventId": *the event id*,
     * "roles": [
     * "*Role1*",
     * "*Role2*",
     * "Role3*"
     * ],
     * "amounts": [
     * *Amount1*,
     * *Amount2*,
     * *Amount3*
     * ]
     * }"
     **/
    @Path("/crewRequired")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response crewRequired(EventRequiresBean required) {
        int eventId = required.getEventId();
        RoleType[] roles = required.getRoles();
        int[] amounts = required.getAmounts();
        try {
            for (int i = 0; i < roles.length; i++) {
                EventDao.instance.overwriteRequired(eventId, roles[i], amounts[i]);
            }
        } catch (SQLException e) {
            return Response.serverError()
                    .build();
        }
        return Response.ok()
                .build();
    }

    private Response enrol(int crewId, int eventId) {
        try {
            RoleType role = CrewMemberDao.I.getRole(crewId);
            int required = EventDao.instance.getRequiredCrewSize(role, eventId);
            int currentEnrolled = EventDao.instance.getCurrentEnrolmentsForRole(role, eventId);
            if (required < currentEnrolled) {
                EventDao.instance.addEnrolment(crewId, eventId);
                return Response.ok()
                        .build();
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
