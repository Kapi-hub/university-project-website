package dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.AccountType;
import models.CrewMemberBean;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public enum AdminDao {
    instance;
    private Connection connection;


    public void createCrewMember(CrewMemberBean crewMember) throws SQLException {
        String query = "INSERT INTO crew_member (id, role, team) VALUES (?, ?::Role, ?::Team)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, crewMember.getId());
        st.setString(2, crewMember.getRole().toString());
        st.setString(3, crewMember.getTeam().toString());
        st.executeUpdate();

        query = "INSERT INTO crew_member (accountId, forename, surname, username, emailAddress, password, accountType) VALUES (?, ?, ?, ?, ?, ?, ?:AccountType)";
        st = connection.prepareStatement(query);
        st.setInt(1, crewMember.getId());
        st.setString(2, crewMember.getForename());
        st.setString(3, crewMember.getSurname());
        st.setString(4, crewMember.getUsername());
        st.setString(5, crewMember.getEmailAddress());
        st.setString(6, crewMember.getPassword());
        st.setString(7, crewMember.getAccountType().toString());
        st.executeUpdate();
    }

}