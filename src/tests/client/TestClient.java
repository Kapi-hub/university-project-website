package client;

import models.ClientBean;
import models.EventBean;
import models.EventType;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

import static dao.ClientDao.I;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClient {

    @Test
    public void testSendingForm() throws SQLException {
        EventBean event = new EventBean("De Reactie", EventType.PHOTOGRAPHY, new Date(System.currentTimeMillis()), new Time(System.currentTimeMillis()), 10, "Evenemenenten Terein Universiteit", 20, -1);
        ClientBean client = new ClientBean("Organisatie", "De Reactie", "organisatie@gmail.com","06123123123");
        assertEquals(event.getClient_id(), -1);
        int id = I.addClient(client);
        assertTrue(id > 0);
        event.setClient_id(id);
        I.addEvent(event);
    }

    @Test
    public void testSendingClient() throws SQLException {
        ClientBean client = new ClientBean("Organisatie", "De Reactie", "organisatie@gmail.com","06123123123");
        assertTrue(client.getClient_id() < 0);
        int id = I.addClient(client);
        assertTrue(id > 0);
    }
}
