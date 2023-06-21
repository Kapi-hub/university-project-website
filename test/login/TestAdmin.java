package login;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.AdminResource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAdmin {


    CrewMemberBean goodCrew;
    @BeforeEach
    public void setCrew() {
        goodCrew = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
    }

    @Test
    void testValidEmail() {
        CrewMemberBean badCrew = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrew1 = new CrewMemberBean("Forename", "Surname", "Username",
                "@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrew2 = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        //officially, no emailaddress can start with a special symbol, hence the following test.
        CrewMemberBean badCrew3 = new CrewMemberBean("Forename", "Surname", "Username",
                ".yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        assertTrue(AdminResource.validEmail(goodCrew));
        assertFalse(AdminResource.validEmail(badCrew));
        assertFalse(AdminResource.validEmail(badCrew1));
        assertFalse(AdminResource.validEmail(badCrew2));
        assertFalse(AdminResource.validEmail(badCrew3));
    }
    @Test
    void testValidPassword() {
        CrewMemberBean badCrewSpace = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0t My R34L Pa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrewNoDigit = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "NotMyReaLPa$$WoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrewShortPassword = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "$h0Rt", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrewNoSymbol = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPassWoRd", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrewNoLower = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0TMYR34LPA$$WORD", RoleType.EDITOR, Team.CORE_AND_CLUB);
        CrewMemberBean badCrewNoUpper = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "n0tmyr34lpa$$word", RoleType.EDITOR, Team.CORE_AND_CLUB);

        assertTrue(AdminResource.validPassword(goodCrew));
        assertFalse(AdminResource.validPassword(badCrewNoDigit));
        assertFalse(AdminResource.validPassword(badCrewShortPassword));
        assertFalse(AdminResource.validPassword(badCrewNoSymbol));
        assertFalse(AdminResource.validPassword(badCrewNoLower));
        assertFalse(AdminResource.validPassword(badCrewNoUpper));
        assertFalse(AdminResource.validPassword(badCrewSpace));
    }
    @Test
    public void addNewCrewMember() {
    }
}
