package resources;

import dao.AccountDao;
import dao.CrewMemberDao;
import dao.EventDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

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
     * @param event the event it will enroll in.
     * @param accountIdString the account id that enrolls
     * @return a response based on what changed in the database
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
     * "crewMember": {
     * "id": *the crewMember's account id*
     * },
     * "event": {
     * "id": *the event id*
     * }
     * }"
     * use the admin to enroll crew members in events
     * @param beans the bean includes all details on where to enroll crew members
     * @return a response based on what changed in the database.
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
     * Gets the required crew.
     * @param required the specific event, role and other information to retrieve the information.
     * @return a response based on success or server error.
     */
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

    /**
     * Retrieves the events associated with a crew for a specific month, based on the provided query parameters.
     * @param QueryDate The date of the month in the format "yyyy-MM" (e.g., "2023-06") to retrieve the events from.
     * @param id The ID of the crew for which to retrieve the events.
     * @return A Response object containing the events associated with the crew for the specified month.
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the provided QueryDate is not in the correct format
     */
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

    /**
     * Retrieves the events for a specific month based on the provided query parameters.
     * @param QueryDate The date of the month in the format "yyyy-MM" (e.g., "2023-06") to retrieve the events from.
     * @param accountIdString The ID of the account associated with the user making the request.
     * @return A Response object containing the events for the specified month.
     * @throws SQLException if a database access error occurs
     */
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
    /**
     * Retrieves the events in which the account with the specified ID is enrolled, filtered by client and month.
     * @param accountIdString The ID of the account associated with the user making the request.
     * @param client The ID of the client to filter the enrolled events (optional).
     * @param month The month to filter the enrolled events (optional).
     * @return A Response object containing the enrolled events based on the provided filters.
     * @throws SQLException if a database access error occurs
     */
    @GET
    @Path("/getEnrolled")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"crew_member"})
    public Response getEnrolled(@CookieParam("accountId") String accountIdString, @QueryParam("client") int client, @QueryParam("month") int month) {
        EventBean[] events;
        try {
            events = EventDao.instance.getEnrolledEvents(Integer.parseInt(accountIdString), client, month);
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

    /**
     * Retrieves the details of a specific event based on the provided event ID.
     * @param id The ID of the event for which to retrieve the details.
     * @return A Response object containing the details of the event.
     */
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

    /**
     * Retrieves the total hours worked by a specific crew member based on the provided member ID.
     * @param id The ID of the crew member for which to retrieve the total hours worked.
     * @return The response with the total hours worked of a crew member
     */
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

    /**
     * Retrieves the total hours worked by a specific crew member based on the provided member ID.
     * @param accountIdString The ID of the crew member for which to retrieve the total hours worked.
     * @return The response with the total hours worked of a crew member
     */
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

    /**
     *
     * Retrieves the missing crew roles and the corresponding number of crew members needed for a specific event based on the provided event ID.
     * @param id The ID of the event for which to retrieve the missing crew roles and members.
     * @return An array of Object arrays representing the missing crew roles and the number of crew members needed.
     */
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

    /**
     * Enrolls a crew member in an event.
     * @param crewId The ID of the crew member to enroll.
     * @param eventId The ID of the event in which to enroll the crew member.
     * @return A Response object indicating the result of the enrollment.
     *     If the crew member is not eligible to enroll in the event, the Response will have a status of 304 (Not Modified).
     *     If the enrollment is successful, the Response will have a status of 200 (OK).
     *     If an error occurs during the enrollment process, the Response will have
     */
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

    /**
     *Checks if a crew member is eligible to enroll in an event.
     * @param eventId The ID of the event for which to check eligibility.
     * @param crewId The ID of the crew member for whom to check eligibility.
     * @return true if the crew member is eligible to enroll in the event, false otherwise.
     */
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

    /**
     * Sends notification to the crew member when unassigned from a booking
     * @param account_id the account ID in question/
     */
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

    /**
     * Get the name from a user based on the name.
     * @param id the ID the admin requests the name for
     * @return the name in json format
     */
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

    /**
     * Returns all the enrolled members based on the event
     * @param eventId the event in question
     * @return the enrolled members
     */
    @GET
    @Path("/crewAssignments/getCrew/{eventId}")
    @RolesAllowed("admin")
    public Object[] getCrewInEvent(@PathParam("eventId") int eventId) {
        return EventDao.instance.getEnrolledMembers(eventId);
    }


    /**
     * Deenrol members as the admin
     * @param crewId the crew member you want to deenrol
     * @param eventId the event Id you want to deenrol them from.
     * @return the response with status
     */
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


    /**
     * Converts an array of EventBean objects to an array of EventResponseBean objects.
     * @param oldBeans The array of EventBean objects to be converted.
     * @return An array of EventResponseBean objects representing the converted EventBean objects.
     */
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
