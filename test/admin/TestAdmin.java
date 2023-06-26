package admin;

import dao.AdminDao;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class TestAdmin {
    @Test
    public void testGetEmailsOfEvent() {
        try {
            int id = 67;
            String[] recipients = AdminDao.I.getEmailsOfEvent(id);
            System.out.println(Arrays.toString(recipients));
            assertNotNull(recipients);
            for (String recipient: recipients) {
                System.out.println(recipient);
            }
        }  catch (SQLException e) {
            System.err.println(e.getMessage());
            fail();
        }
    }
}
