package resources;

import dao.AccountDao;
import dao.CrewMemberDao;
import dao.EventDao;
import dao.MailService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

import javax.mail.MessagingException;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dao.MailService.MAIL;


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
    @Path("/getFromMonthAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response getCrewsEventsFromMonth(@QueryParam("month") String QueryDate, @QueryParam("eventId") int id){
        EventBean[] events;
        try {
            events = EventDao.instance.getFromMonth(Timestamp.valueOf(QueryDate + "-01 00:00:00"));
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Timestamp: " + QueryDate + "-01 00:00:00");
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
        if (events == null) {
            return Response.noContent()
                    .build();
        }
        EventResponseBean[] finalBeans = beansToBeans(events);
        for (EventResponseBean bean : finalBeans) {
            bean.setCanEnrol(canEnrol(bean.getId(), id));
            bean.setIsEnrolled(EventDao.instance.isEnrolled(id, bean.getId()));
        }
        return Response.ok(finalBeans)
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
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Timestamp: " + QueryDate + "-01 00:00:00");
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
        if (events == null) {
            return Response.noContent()
                    .build();
        }
        EventResponseBean[] finalBeans = beansToBeans(events);
        for(EventResponseBean bean : finalBeans) {
            bean.setCanEnrol(canEnrol(bean.getId(), Integer.parseInt(accountIdString)));
            bean.setIsEnrolled(EventDao.instance.isEnrolled(Integer.parseInt(accountIdString), bean.getId()));
        }
        return Response.ok(finalBeans)
                .build();
    }

    @GET
    @Path("/getEnrolled")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"crew_member"})
    public Response getEnrolled(@CookieParam("accountId") String accountIdString, @QueryParam("client") int client, @QueryParam("month") int month) {
        EventBean[] events;
        try {
            events = EventDao.instance.getEnrolledEvents(Integer.parseInt(accountIdString));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
        if (events == null) {
            return Response.noContent()
                    .build();
        }
        EventResponseBean[] returnValue = beansToBeans(events);
        for(EventResponseBean bean : returnValue) {
            bean.setCanEnrol(false);
            bean.setIsEnrolled(true);
        }
        return Response.ok(returnValue)
                .build();
    }

    @Path("/getCrew/{eventId}")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getDetailsOfEvent(@PathParam("eventId") int id) {
        EventBean[] events;
        try {
            events = EventDao.instance.getAllDetails(id);
        } catch (SQLException e) {
            return Response.serverError()
                    .build();
        }
        if (events == null) {
            return Response.noContent()
                    .build();
        }
        EventResponseBean[] finalBeans = beansToBeans(events);

        return Response.ok(finalBeans)
                .build();
    }

    @Path("/getCrewHoursWorked/{memberId}")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getHoursWorked(@PathParam("memberId") int id) {
        try {
            return Response.ok(EventDao.instance.getHoursWorked(id))
                    .build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
    }

    @Path("/getHoursWorked")
    @RolesAllowed("crew_member")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getHoursWorked(@CookieParam("accountId") String accountIdString) {
        int accountId = Integer.parseInt(accountIdString);
        try {
            return Response.ok(EventDao.instance.getHoursWorked(accountId))
                    .build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.serverError()
                    .build();
        }
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
        if (EventDao.instance.isEnrolled(crewId, eventId)) {
            return false;
        }
        try {
            EventStatus status = EventDao.instance.getEventStatus(eventId);
            if (status != EventStatus.REVIEW || EventDao.instance.isEventInPast(eventId)) {
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

    private void sendNotificationToCrewMember(int account_id) {
        String recipient;
        try {
            recipient = AccountDao.instance.getEmailAddressById(account_id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return;
        }
        String subject = "You have been unassigned from a booking.";
        String body =   """
                        Dear Crew member,
                                        
                        You have been unassigned from a booking. 
                        
                        Please consult the dashboard.
                        
                        Sincerely,
                        The computer behind Shotmaniacs.
                        """;
        try {
            MAIL.sendMessage(recipient, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            System.err.println(e.getMessage());
        }
    }

    @GET
    @Path("/getName/{id}")
    @RolesAllowed("admin")
    public String getNameFromId(@PathParam("id") int id) {
        String name = null;
        try {
            name = AccountDao.instance.getNamesAsJSON(id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return name;
        }
        return name;
    }

    @GET
    @Path("/crewAssignments/getCrew/{eventId}")
    @RolesAllowed("admin")
    public Object[] getCrewInEvent(@PathParam("eventId") int eventId) {
        return EventDao.instance.getEnrolledMembers(eventId);
    }


    @DELETE
    @Path("/deenrol/{crewId}/{eventId}")
    @RolesAllowed("admin")
    public Response unenrol(@PathParam("crewId") int crewId, @PathParam("eventId") int eventId) {
        if (!EventDao.instance.isEnrolled(crewId, eventId)) {
            return Response.notModified()
                    .build();
        }
        try {
            EventStatus status = EventDao.instance.getEventStatus(eventId);
            if (status == EventStatus.DONE) {
                return Response.notModified()
                        .build();
            }
            EventDao.instance.removeEnrolment(crewId, eventId);
            sendNotificationToCrewMember(crewId);
            return Response.ok()
                    .build();
        } catch (SQLException e) {
            return Response.serverError()
                    .build();
        }
    }



    private EventResponseBean[] beansToBeans(EventBean[] oldBeans) {
        EventResponseBean[] newBeans = new EventResponseBean[oldBeans.length];
        for (int i = 0; i < oldBeans.length; i++) {
            int id = oldBeans[i].getId();
            String name = oldBeans[i].getName();
            EventType type = oldBeans[i].getType();
            Timestamp date = oldBeans[i].getStart();
            String location = oldBeans[i].getLocation();
            int duration = oldBeans[i].getDuration();
            String client = AccountDao.instance.getName(oldBeans[i].getClient_id());
            BookingType bookingType = oldBeans[i].getBooking_type();
            String productionManager = AccountDao.instance.getName(oldBeans[i].getProduction_manager_id());
            Object[] crew = getMissingCrew(id);
            Object[] enrolled = EventDao.instance.getEnrolledMembers(id);
            EventStatus status = oldBeans[i].getStatus();
            String description = oldBeans[i].getDescription();
            newBeans[i] = new EventResponseBean(id, name, type, date, location, duration, client, bookingType, productionManager, crew, enrolled, status, description);
        }
        return newBeans;
    }
}
