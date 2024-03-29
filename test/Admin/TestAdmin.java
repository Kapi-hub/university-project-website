package Admin;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.core.Response;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.AdminResource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TestAdmin {


    CrewMemberBean goodCrew;
    AdminResource resource;
    @BeforeEach
    void setCrew() {
        resource = new AdminResource();
        goodCrew = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.PRODUCER, Team.CLUB);
        resource.handleCreateNewMember(goodCrew);
    }
    @Test
    void testValidEmail() {
        CrewMemberBean badCrew = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrew1 = new CrewMemberBean("Forename", "Surname", "Username",
                "@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrew2 = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE);
        //officially, no emailaddress can start with a special symbol, hence the following test.
        CrewMemberBean badCrew3 = new CrewMemberBean("Forename", "Surname", "Username",
                ".yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE);
        assertTrue(AdminResource.validEmail(goodCrew));
        assertFalse(AdminResource.validEmail(badCrew));
        assertFalse(AdminResource.validEmail(badCrew1));
        assertFalse(AdminResource.validEmail(badCrew2));
        assertFalse(AdminResource.validEmail(badCrew3));
    }
    @Test
    void testValidPassword() {
        CrewMemberBean badCrewSpace = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0t My R34L Pa$$WoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewNoDigit = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "NotMyReaLPa$$WoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewShortPassword = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "$h0Rt", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewNoSymbol = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPassWoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewNoLower = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0TMYR34LPA$$WORD", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewNoUpper = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "n0tmyr34lpa$$word", RoleType.EDITOR, Team.CORE);

        assertTrue(AdminResource.validPassword(goodCrew));
        assertFalse(AdminResource.validPassword(badCrewNoDigit));
        assertFalse(AdminResource.validPassword(badCrewShortPassword));
        assertFalse(AdminResource.validPassword(badCrewNoSymbol));
        assertFalse(AdminResource.validPassword(badCrewNoLower));
        assertFalse(AdminResource.validPassword(badCrewNoUpper));
        assertFalse(AdminResource.validPassword(badCrewSpace));
    }
    @Test
    void testHandleCreateNewMember() {
        CrewMemberBean badCrewInvalidPassword = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0t My R34L Pa$$WoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewInvalidEmail = new CrewMemberBean("Forename", "Surname", "Username",
                ".yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE);
        assertEquals(Response.ok().build().getEntity(), resource.handleCreateNewMember(goodCrew).getEntity());
        assertEquals(Response.status(400).build().getEntity(), resource.handleCreateNewMember(badCrewInvalidPassword).getEntity());
        assertEquals(Response.status(400).build().getEntity(), resource.handleCreateNewMember(badCrewInvalidEmail).getEntity());
    }

//    @Test
//    void testhandlePostAnnouncement(announcement, accountIdString) {
//
//    }

}
