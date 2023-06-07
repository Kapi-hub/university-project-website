package dao;

import misc.ConnectionFactory;
import models.ClientBean;
import models.EventBean;

import models.EventType;

import java.sql.*;

/**
 * Singleton Pattern // Data access Object
 */
public enum ClientDao {
    I;

    Connection connection;

    ClientDao() {
        connection = ConnectionFactory.getConnection();
    }

    public int addClient(ClientBean client) throws SQLException {
        String query = "INSERT INTO account (id, forename, surname, username, email_address) VALUES (DEFAULT,?,?,?,?,'client'::account_type) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, client.getForename());
        st.setString(2, client.getSurname());
        st.setString(3, client.getUsername());
        st.setString(4, client.getEmailAddress());
        ResultSet rs = st.executeQuery();
        System.out.println("===SQL=== ADDED A CLIENT TO DATABASE");
        int client_id = -1;
        if (rs.next()) {
            client_id = rs.getInt(1);
        }
        query = "INSERT INTO client (id, phone_number) VALUES (?, ?)";
        st = connection.prepareStatement(query);
        st.setInt(1, client_id);
        st.setString(2, client.getPhone_number());
        return client_id;
    }

    public void addEvent(EventBean event) throws SQLException {
        String query = "INSERT INTO event (client_id, name, description, start, duration, location, type) VALUES (?,?,?,?,?,?, ?::event_type)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, event.getClient_id());
        st.setString(2, event.getName());
        st.setString(3, event.getDescription());
        st.setTimestamp(4, event.getStart());
        st.setInt(5, event.getDuration());
        st.setString(6, event.getLocation());
        st.setString(7, event.getType().toString());
        st.executeUpdate();
        System.out.println("===SQL=== ADDED AN EVENT TO DATABASE");
    }
}

