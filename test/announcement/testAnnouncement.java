package announcement;

import dao.AnnouncementDao;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.AnnouncementResource;

import java.sql.SQLException;

public class testAnnouncement {

//    AnnouncementBean announcementBean;
//    AnnouncementResponseBean announcementResponseBean;
//    @BeforeEach
//    void setup() {
//        announcementBean = new AnnouncementBean(1, 2,  "title",
//                "body", new Timestamp(3,4,5,6,7,8,9));
//        announcementResponseBean = new AnnouncementResponseBean();
//    }

    @Test
    void testGetAnnouncementsForCrew() throws SQLException {
        AnnouncementResource resource = new AnnouncementResource();
        //matching IDs
        Assertions.assertEquals(Response.ok(AnnouncementDao.instance.getAnnouncementsForCrew(1)).build().toString(),
                resource.getAnnouncementsForCrew("1").toString());
        //IDs do not match
        Assertions.assertNotEquals(Response.ok(AnnouncementDao.instance.getAnnouncementsForCrew(1)).build(),
                resource.getAnnouncementsForCrew("2"));
        //nonexistent account //TODO fix this bottom one
        Assertions.assertEquals((Response.status(Response.Status.NO_CONTENT).build()).toString(),
                (resource.getAnnouncementsForCrew("-8")).toString());

    }
}
