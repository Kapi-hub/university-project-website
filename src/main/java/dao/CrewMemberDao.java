package dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.AccountType;
import models.CrewMemberBean;


import java.sql.PreparedStatement;
import java.sql.SQLException;


public enum CrewMemberDao {
    Connection connection;



    public void createCrewMember(CrewMemberBean crewMember) throws SQLException {
        String query = "INSERT INTO crew_member (id, role, team) VALUES (?, ?::Role, ?::Team)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, crewMember.getId());
        st.setString(2, crewMember.getRole().toString());
        st.setString(3, crewMember.getTeam().toString());
        st.executeUpdate();

        query = "INSERT INTO crew_member (accountId, forename, surname, username, emailAddress, password, phoneNumber, sesssionId, accountType) VALUES (?, ?, ?, ?, ?, ?, ?, ?:AccountType)";
        st = connection.prepareStatement(query);
        st.setInt(1, crewMember.getId());
        st.setString(2, crewMember.getForename());
        st.setString(3, crewMember.getSurname());
        st.setString(4, crewMember.getUsername());
        st.setString(5, crewMember.getPassword());
        st.setString(6, crewMember.getPhoneNumber());
        st.setString(7, crewMember.getSessionId());
        st.setString(8, crewMember.getAccountType().toString());
        st.executeUpdate();
    }
}
