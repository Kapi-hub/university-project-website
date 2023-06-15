package resources;

import dao.CrewMemberDao;
import dao.EventDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.*;

import java.sql.SQLException;
import java.sql.Timestamp;

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

    @GET
    @Path("/getFromDate")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "crew_member"})
    public Response getFromDate(@QueryParam("date") String date) {
//        EventBean[] events = null;
//        try {
//            events = EventDao.instance.getFromDate(Timestamp.valueOf(date));
//        } catch (SQLException e) {
//            return Response.serverError()
//                    .build();
//        }
//        if (events == null) {
//            return Response.noContent()
//                    .build();
//        }
        String finalReturn = """
                [
                  {
                    "id": 1,
                    "name": "Event 1",
                    "type": "Type1",
                    "date": "2023-06-15T00:00:00.000Z",
                    "location": "Location1",
                    "duration": 2,
                    "client": "Client1",
                    "bookingType": "BookingType1",
                    "productManager": "ProductManager1",
                    "crew": [
                      ["CrewType1", 2],
                      ["CrewType2", 3]
                    ],
                    "enrolled": [
                      ["Role1", ["Person1", "Person2"]],
                      ["Role2", ["Person3", "Person4"]]
                    ],
                    "status": "Status1",
                    "description": "Description1"
                  },
                  {
                    "id": 2,
                    "name": "Event 2",
                    "type": "Type2",
                    "date": "2023-06-15T00:00:00.000Z",
                    "location": "Location2",
                    "duration": 3,
                    "client": "Client2",
                    "bookingType": "BookingType2",
                    "productManager": "ProductManager2",
                    "crew": [
                      ["CrewType3", 4],
                      ["CrewType4", 5]
                    ],
                    "enrolled": [
                      ["Role3", ["Person5", "Person6"]],
                      ["Role4", ["Person7", "Person8"]]
                    ],
                    "status": "Status2",
                    "description": "Description2"
                  }
                ]""";
        System.out.println("returning the events");
        return Response.ok(finalReturn)
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
