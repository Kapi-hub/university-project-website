package client;

import models.BookingType;
import models.ClientBean;
import models.EventBean;
import models.EventType;
import org.junit.jupiter.api.Test;
import resources.ClientResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;

import static dao.ClientDao.I;
import static org.junit.jupiter.api.Assertions.*;

public class TestClient {

    @Test
    public void testSendingForm() throws SQLException {
        EventBean event = new EventBean("De Reactie", "De reactie beschrijving", new Timestamp(System.currentTimeMillis()), 10, "Evenemenenten Terein Universiteit", EventType.CLUB_PHOTOGRAPHY, BookingType.PHOTOGRAPHY);
        ClientBean client = new ClientBean("Organisatie", "De Reactie", "organisatie_de_reactie", "organisatie@gmail.com","06123123123");
        assertTrue(event.getClient_id() <= 0);
        int id = I.addClient(client);
        assertTrue(id > 0);
        event.setClient_id(id);
        I.addEvent(event);
    }

    @Test
    public void testSendingClient() throws SQLException {
        ClientBean client = new ClientBean("Organisatie", "De Reactie", "organisatie_de_reactie", "organisatie@gmail.com","06123123123");
        assertTrue(client.getId() < 0);
        int id = I.addClient(client);
        assertTrue(id > 0);
    }

    @Test
    public void testHandleCSV() {
        try {
            InputStream inputStream = new FileInputStream("src/main/webapp/WEB-INF/publicResources/client/template/Template_example.csv");
            new ClientResource().handleCsvFile(inputStream, 190);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testHandleXlsx() {

        try {
            InputStream inputStream = new FileInputStream("src/main/webapp/WEB-INF/publicResources/client/template/Template_example.xlsx");
            new ClientResource().handleExcelFile(inputStream, 190);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            fail();
        }
    }
}
