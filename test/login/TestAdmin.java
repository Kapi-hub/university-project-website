package login;

import dao.AdminDao;
import misc.ConnectionFactory;
import models.CrewMemberBean;
import org.junit.jupiter.api.BeforeAll;
import jakarta.ws.rs.core.Response;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.AdminResource;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static misc.Security.checkPassword;
import static org.junit.jupiter.api.Assertions.*;

public class TestAdmin {

    static Connection connection;
    CrewMemberBean crew;

    @BeforeAll
    public static void setup() {
        connection = ConnectionFactory.getConnection();
        assertNotNull(connection);
    }


    CrewMemberBean goodCrew;
    @BeforeEach
    public void setCrew() {
        crew = new CrewMemberBean(
                "Crew", "member", generateRandomString(20), "crew_member123@gmail.com",
                RoleType.PHOTOGRAPHER, Team.CORE);
    }
    public void setCrew() {
        goodCrew = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CLUB);
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
//        assertFalse(AdminResource.validEmail(badCrew));
//        assertFalse(AdminResource.validEmail(badCrew1));
//        assertFalse(AdminResource.validEmail(badCrew2));
//        assertFalse(AdminResource.validEmail(badCrew3));
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
        AdminResource admin = new AdminResource();
        CrewMemberBean badCrewInvalidPassword = new CrewMemberBean("Forename", "Surname", "Username",
                "b.yilmaz-1@student.utwente.nl", "N0t My R34L Pa$$WoRd", RoleType.EDITOR, Team.CORE);
        CrewMemberBean badCrewInvalidEmail = new CrewMemberBean("Forename", "Surname", "Username",
                ".yilmaz-1@student.utwente.nl", "N0tMyR34LPa$$WoRd", RoleType.EDITOR, Team.CORE);
        assertEquals(Response.ok().build().getEntity(), admin.handleCreateNewMember(goodCrew).getEntity());
        assertEquals(Response.status(400).build().getEntity(), admin.handleCreateNewMember(badCrewInvalidPassword).getEntity());
        assertEquals(Response.status(400).build().getEntity(), admin.handleCreateNewMember(badCrewInvalidEmail).getEntity());
    }

    @Test
    public void addNewCrewMember() throws GeneralSecurityException, SQLException {
        assertNotNull(crew);
        assertNull(crew.getPassword());
        String password = "password";
        crew.setPassword(password);
        AdminDao.I.createNewMember(crew);
        String query = "SELECT password, salt FROM account WHERE username LIKE ?";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, crew.getUsername());
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String retrievedPassword = rs.getString(1);
            String retrievedSalt = rs.getString(2);
            assertNotEquals(password, retrievedPassword);
            assertTrue(checkPassword(password, retrievedSalt, retrievedPassword));
        } else fail();
    }

    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
