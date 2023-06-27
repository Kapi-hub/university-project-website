package announcement;

import jakarta.ws.rs.core.Response;
import models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.AnnouncementResource;

import java.sql.Timestamp;


public class testAnnouncement {

//    AnnouncementBean announcementBean;
//    AnnouncementResponseBean announcementResponseBean;
//    @BeforeEach
//    void setup() {
//        announcementBean = new AnnouncementBean(1, 2,  "title",  "body", new Timestamp(1,1,1,1,1,1,1));
//        announcementResponseBean = new AnnouncementResponseBean();
//    }

    @Test
    void testGetAnnouncementsForCrew() {
        AnnouncementResource resource = new AnnouncementResource();
        Assertions.assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(), resource.getAnnouncementsForCrew("1")); //needs String, what is argument?
        Assertions.assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(), resource.getAnnouncementsForCrew("2")); //needs String, what is argument?
        //nonexistent account
        Assertions.assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(), resource.getAnnouncementsForCrew("-4")); //needs String, what is argument?


    }
}
