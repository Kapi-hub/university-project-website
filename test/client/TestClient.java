package client;

import models.BookingType;
import models.ClientBean;
import models.EventBean;
import models.EventType;
import org.junit.jupiter.api.Test;
import resources.ClientResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;

import static dao.ClientDao.I;
import static org.junit.jupiter.api.Assertions.*;

 class TestClient {

    @Test
     void testHandleCSV() {
        try {
            InputStream inputStream = new FileInputStream("src/main/webapp/WEB-INF/publicResources/client/template/Template_example.csv");
            new ClientResource().handleCsvFile(inputStream, 190);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            fail();
        }
    }

    @Test
     void testHandleXlsx() {

        try {
            InputStream inputStream = new FileInputStream("src/main/webapp/WEB-INF/publicResources/client/template/Template_example.xlsx");
            new ClientResource().handleExcelFile(inputStream, 190);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            fail();
        }
    }
}
