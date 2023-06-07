package dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.AccountType;
import models.CrewMemberBean;
import models.Role;
import models.Team;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public enum CrewMemberDao {
    Connection connection;

    private int accountId;
    private String forename;
    private String surname;
    @JsonProperty("username")
    private String username;
    private String emailAddress;
    @JsonProperty("password")
    private String password;
    private String phoneNumber;
    private String sessionId;
    private AccountType accountType;

    private String id;
    private Role role;
    private Team team;

    public void createCrewMember(CrewMemberBean crewMember) throws SQLException {
        String query = "INSERT INTO crew_member (id, role, team) VALUES (?, ?, ?)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, crewMember.getId());
        st.setRole(2, crewMember.getRole());

    }
}
