package event;

import jakarta.ws.rs.core.Response;
import models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.EventResource;

import java.awt.print.Book;
import java.sql.Timestamp;

class TestEvent {

    CrewMemberBean goodCrew;
    CrewMemberBean manager;
    ClientBean goodClient;
    EventBean goodEvent;
    EventResource eventResource;
    @BeforeEach
    void setup() {
        eventResource = new EventResource();

        manager = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.PRODUCER, Team.CLUB);
        manager.setId(-42);

        goodCrew = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.PHOTOGRAPHER, Team.CLUB);
        goodCrew.setId(-69);

        goodClient = new ClientBean();
        goodClient.setId(-420);

        goodEvent = new EventBean(100, -420, "Test Event",
                "Test Description",
                new Timestamp(2023,8,20,14,0,0,0),
                4, "Enschede", -42,
                EventType.PRODUCT_SHOOT, BookingType.PHOTOGRAPHY, EventStatus.IN_PROGRESS);
    }
    @Test
    void testEnrol() {
        Assertions.assertEquals(Response.ok().build(),
        eventResource.enrol(-69, -420));
    }
    @Test
    void testEnrollSelf() {

    }

    @Test
    void testGetEnrolled() {

    }

    @Test
    void testGetHoursWorked () {

    }

}
