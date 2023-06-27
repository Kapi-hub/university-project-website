package announcement;

import models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.AnnouncementResource;

import java.sql.Timestamp;


public class testAnnouncement {

    AnnouncementBean announcementBean;
    AnnouncementResponseBean announcementResponseBean;


    @BeforeEach
    void setup() {
        announcementBean = new AnnouncementBean(1, 2,  "title",  "body", new Timestamp(1,1,1,1,1,1,1));
        announcementResponseBean = new AnnouncementResponseBean();
    }

    @Test
    void testGetAnnouncementsForCrew() {
        AnnouncementResource resource = new AnnouncementResource();
        Assertions.assertEquals(, resource.getAnnouncementsForCrew(announcementResponseBean.getI));
    }
}
