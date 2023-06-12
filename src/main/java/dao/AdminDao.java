package dao;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import models.AccountType;
import models.CrewMemberBean;
import models.EventBean;
import models.RequiredCrewBean;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public enum AdminDao {
    I;
    final Connection connection;

    AdminDao() {
        connection = ConnectionFactory.getConnection();
    }

    public Response createNewMember(CrewMemberBean crewMember) {
        String insertCrewQuery =  "INSERT INTO crewMember(id, role, team) VALUES (?,?::Role,?::Team)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertCrewQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getRole().toString());
            st.setString(3, crewMember.getTeam().toString()) ;
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        String insertAccountQuery =  "INSERT INTO account(id, forename, surname, username, email_address, password, type) VALUES (?,?, ?, ?, ? ?, ?::AccountType)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertAccountQuery);
            st.setInt(1, crewMember.getId());
            st.setString(2,crewMember.getForename());
            st.setString(3, crewMember.getSurname()) ;
            st.setString(4, crewMember.getUsername()) ;
            st.setString(5, crewMember.getEmailAddress());
            st.setString(6, crewMember.getPassword());
            st.setString(7, AccountType.CREW_MEMBER.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        //TODO handle responses
        return Response.accepted().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createNewEvent(EventBean event) {
        String insertEventQuery =  "INSERT INTO event(id, name, description, start, duration, location, type) VALUES (?,?,?,?,?,?::event_type_enum)" ;
        try {
            PreparedStatement st = connection.prepareStatement(insertEventQuery);
            st.setInt(1, event.getId());
            st.setString(2,event.getName());
            st.setString(3, event.getDescription()) ;
            st.setTimestamp(4, event.getStart()); ;
            st.setString(5, event.getLocation()) ;
            st.setString(6,event.getType().toString());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addRequirement(List<RequiredCrewBean> required) throws SQLException {
        for(RequiredCrewBean requiredCrewBean:required) {
            String query = "INSERT INTO event_requirement (event_id, crew_size, role) VALUES (?, ?, ?::role_enum)";
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, requiredCrewBean.getEvent_id());
            st.setInt(2, requiredCrewBean.getCrew_size());
            st.setString(3, requiredCrewBean.getRole().toString());
            st.executeUpdate();
        }

    }

}