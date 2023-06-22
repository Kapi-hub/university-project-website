package login;

import dao.AdminDao;
import misc.ConnectionFactory;
import models.CrewMemberBean;
import models.RoleType;
import models.Team;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    @BeforeEach
    public void setCrew() {
        crew = new CrewMemberBean(
                "Crew", "member", generateRandomString(20), "crew_member123@gmail.com",
                RoleType.PHOTOGRAPHER, Team.CORE);
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
