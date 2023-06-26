package client;

import login.TestAdmin;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import static dao.ClientDao.I;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClient {
    String username;
    @BeforeEach
    public void setCrew() {
        username = TestAdmin.generateRandomString(20);
    }

    @Test
    public void testSendingForm() throws SQLException {
        EventBean event = new EventBean("De Reactie", "De reactie beschrijving", new Timestamp(System.currentTimeMillis()), 10, "Evenemenenten Terein Universiteit", EventType.CLUB_PHOTOGRAPHY, BookingType.PHOTOGRAPHY);
        ClientBean client = new ClientBean("Organisatie", "De Reactie", username, "organisatie@gmail.com","06123123123");
        assertTrue(event.getClient_id() <= 0);
        int id = I.addClient(client);
        assertTrue(id > 0);
        event.setClient_id(id);
        I.addEvent(event);
    }

    @Test
    public void testSendingClient() throws SQLException {
        ClientBean client = new ClientBean("Organisatie", "De Reactie", username, "organisatie@gmail.com","06123123123");
        System.out.println(client.getId());
        assertEquals(0, client.getId());
        int id = I.addClient(client);
        assertTrue(id > 0);
    }
}
