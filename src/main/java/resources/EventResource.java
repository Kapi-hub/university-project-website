package resources;

import dao.AccountDao;
import dao.CrewMemberDao;
import dao.EventDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        System.out.println("Enrolling self in event " + event.getId());
        int accountId = Integer.parseInt(accountIdString);
        return enrol(accountId, event.getId());
    }

    /**
     * In JSON, use syntax:
     * "{
     * "id": *the event id*
     * }"
     */
    @Path("/unenroll-self")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("crew_member")
    public Response unenrollSelf(EventBean event, @CookieParam("accountId") String accountIdString) {
        System.out.println("Unenrolling self from event " + event.getId());
        int accountId = Integer.parseInt(accountIdString);
        return unenrol(accountId, event.getId());
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

    @GET
    @Path("/getFromMonth")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "crew_member"})
    public Response getFromMonth(@QueryParam("month") String QueryDate, @CookieParam("accountId") String accountIdString) {
        EventBean[] events;
        try {
            events = EventDao.instance.getFromMonth(Timestamp.valueOf(QueryDate + "-01 00:00:00"));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
        if (events == null) {
            return Response.noContent()
                    .build();
        }
        EventResponseBean[] finalBeans = new EventResponseBean[events.length];
        for (int i = 0; i < events.length; i++) {
            int id = events[i].getId();
            String name = events[i].getName();
            EventType type = events[i].getType();
            Timestamp date = events[i].getStart();
            String location = events[i].getLocation();
            int duration = events[i].getDuration();
            String client = AccountDao.instance.getName(events[i].getClient_id());
            BookingType bookingType = events[i].getBooking_type();
            String productionManager = AccountDao.instance.getName(events[i].getProduction_manager_id());
            Object[] crew = getMissingCrew(id);
            Object[] enrolled = EventDao.instance.getEnrolled(id);
            EventStatus status = events[i].getStatus();
            String description = events[i].getDescription();
            boolean isEnrolled = EventDao.instance.isEnrolled(Integer.parseInt(accountIdString), id);
            boolean canEnrol = canEnrol(id, Integer.parseInt(accountIdString));
            finalBeans[i] = new EventResponseBean(id, name, type, date, location, duration, client, bookingType, productionManager, crew, enrolled, status, description, isEnrolled, canEnrol);
        }
        return Response.ok(finalBeans)
                .build();
    }

    private Object[] getMissingCrew(int id) {
        Map<RoleType, Integer> required;
        try {
            required = EventDao.instance.getRequiredMap(id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        Set<RoleType> requiredKeysCopy = new HashSet<>(required.keySet());
        for (RoleType role : requiredKeysCopy) {
            int currentEntry = required.get(role);
            try {
                int currentEnrolments = EventDao.instance.getCurrentEnrolmentsForRole(role, id);
                required.replace(role, currentEntry - currentEnrolments);
            } catch (SQLException ignored) {
                // the sqlexception means nobody is enrolled for this role, we can therefore safely continue
            }
            if (required.get(role) <= 0) {
                required.remove(role);
            }
        }

        if (required.size() == 0) {
            return null;
        }

        Object[] returnValue = new Object[required.size()];
        int i = 0;
        for (RoleType role : required.keySet()) {
            Object[] roleRequiredPair = new Object[2];
            roleRequiredPair[0] = role;
            roleRequiredPair[1] = required.get(role);

            returnValue[i] = roleRequiredPair;
            i++;
        }

        return returnValue;
    }

    private Response enrol(int crewId, int eventId) {
        if (!canEnrol(eventId, crewId)) {
            return Response.notModified()
                    .build();
        }
        try {
            EventDao.instance.addEnrolment(crewId, eventId);
            return Response.ok()
                    .build();
        } catch (SQLException e) {
            return Response.serverError()
                    .build();
        }
    }

    private boolean canEnrol(int eventId, int crewId) {
        try {
            if (EventDao.instance.isEnrolled(crewId, eventId)) {
                return false;
            }
            EventStatus status = EventDao.instance.getEventStatus(eventId);
            if (status != EventStatus.REVIEW) {
                return false;
            }
            RoleType role = CrewMemberDao.I.getRole(crewId);
            int required = EventDao.instance.getRequiredCrewSize(role, eventId);
            int currentEnrolled = EventDao.instance.getCurrentEnrolmentsForRole(role, eventId);
            return required > currentEnrolled;
        } catch (SQLException e) {
            return false;
        }
    }

    @GET
    @Path("/crewAssignmemts/getCrew/{eventId}")
    @RolesAllowed("admin")
    public Object[] getCrewInEvent(@PathParam("eventId") int eventId) {
        return EventDao.instance.getEnrolled(eventId);
    }

    @DELETE
    @Path("/crewAssignments/deenrol/{crewId}/{eventId}")
    @RolesAllowed("admin")
    public Response unenrol(@PathParam("crewId") int crewId, @PathParam("eventId") int eventId) {
        if (!EventDao.instance.isEnrolled(crewId, eventId)) {
            return Response.notModified()
                    .build();
        }
        try {
            EventDao.instance.removeEnrolment(crewId, eventId);
            return Response.ok()
                    .build();
        } catch (SQLException e) {
            return Response.serverError()
                    .build();
        }
    }
}
