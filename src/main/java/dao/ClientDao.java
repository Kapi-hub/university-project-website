package dao;

import misc.ConnectionFactory;
import models.ClientBean;
import models.EventBean;

import models.EventType;
import models.RequiredCrewBean;

import java.sql.*;

/**
 * Singleton Pattern // Data access Object
 */
public enum ClientDao {
    I;

    final Connection connection;

    ClientDao() {
        connection = ConnectionFactory.getConnection();
    }

    public int addClient(ClientBean client) throws SQLException {
        String query = "INSERT INTO account (forename, surname, username, email_address, type) VALUES (?,?,?,?,'client'::account_type) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, client.getForename());
        st.setString(2, client.getSurname());
        st.setString(3, client.getUsername());
        st.setString(4, client.getEmailAddress());
        ResultSet rs = st.executeQuery();
        System.out.println("===SQL=== ADDED A CLIENT TO ACCOUNT");
        int client_id = -1;
        if (rs.next()) {
            client_id = rs.getInt(1);
        }
        System.out.println(client_id);
        query = "INSERT INTO client (id, phone_number) VALUES (?, ?)";
        st = connection.prepareStatement(query);
        st.setInt(1, client_id);
        st.setString(2, client.getPhone_number());
        st.executeUpdate();
        System.out.println(" ==SQL== ADDED A CLIENT TO CLIENT TABLE");
        return client_id;
    }

    public int addEvent(EventBean event) throws SQLException {
        String query = "INSERT INTO event (client_id, name, description, start, duration, location, type) VALUES (?,?,?,?,?,?, ?::event_type) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, event.getClient_id());
        st.setString(2, event.getName());
        st.setString(3, event.getDescription());
        st.setTimestamp(4, event.getStart());
        st.setInt(5, event.getDuration());
        st.setString(6, event.getLocation());
        st.setString(7, event.getType().toString());


        ResultSet rs = st.executeQuery();
        System.out.println("===SQL=== ADDED AN EVENT TO DATABASE");
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }

    public void addRequirement(RequiredCrewBean required) throws SQLException {
        String query = "INSERT INTO event_requirement (event_id, crew_size, role) VALUES (?, ?, ?::role)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, required.getEvent_id());
        st.setInt(2, required.getCrew_size());
        st.setString(3, required.getRole().toString());
        st.executeUpdate();
        System.out.println("===SQL=== ADDED A ROLE REQUIRED TO DATABASE");
    }
}

