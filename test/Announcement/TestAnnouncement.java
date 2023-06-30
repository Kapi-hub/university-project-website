package Announcement;

import dao.AnnouncementDao;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.AnnouncementResource;

import java.sql.SQLException;

class TestAnnouncement {

    @Test
    void testGetAnnouncementsForCrew() throws SQLException {
        AnnouncementResource resource = new AnnouncementResource();
        //matching IDs
        Assertions.assertEquals(
                Response.ok(AnnouncementDao.instance.getAnnouncementsForCrew(1)).build().toString(),
                resource.getAnnouncementsForCrew("1").toString());
        //IDs do not match
        Assertions.assertEquals(
                Response.ok(AnnouncementDao.instance.getAnnouncementsForCrew(1)).build().toString(),
                resource.getAnnouncementsForCrew("2").toString());
    }
}
