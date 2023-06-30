package dao;

import misc.ConnectionFactory;
import models.ClientBean;
import models.EventBean;

import models.RequiredCrewBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to modify data relating to the client
 * Singleton Pattern // Data access Object
 */
public enum ClientDao {
    /**
     * Enum constant to make sure only one instance exists that handles the code.
     */
    I;

    /**
     * The connection retrieved by the connection Factory.
     */
    final Connection connection;

    /**
     * Constructor to set up connection
     */
    ClientDao() {
        connection = ConnectionFactory.getConnection();
    }

    /**
     * Adds a client to the database
     * @param client a client object to define the data necessary
     * @return the client_id of which it has been generated
     * @throws SQLException occurs when an SQL issue arises.
     */
    public int addClient(ClientBean client) throws SQLException {
        String query = "INSERT INTO account (forename, surname, username, email_address, type) VALUES (?,?,?,?,'client'::account_type_enum) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, client.getForename());
        st.setString(2, client.getSurname());
        st.setString(3, client.getUsername());
        st.setString(4, client.getEmailAddress());
        ResultSet rs = st.executeQuery();

        int client_id = -1;
        if (rs.next()) {
            client_id = rs.getInt(1);
        }
        System.out.printf("===SQL=== ADDED A CLIENT TO ACCOUNT TABLE, RETURNED ID %s.\n", client_id);
        query = "INSERT INTO client (id, phone_number) VALUES (?, ?)";
        st = connection.prepareStatement(query);
        st.setInt(1, client_id);
        st.setString(2, client.getPhone_number());
        st.executeUpdate();
        System.out.printf("===SQL=== ADDED A CLIENT TO CLIENT TABLE WITH ID %s\n", client_id);
        return client_id;
    }

    /**
     * Adds an event to the database
     * @param event an event object to define the data necessary
     * @return the event_id of which it has been generated
     * @throws SQLException occurs when an SQL issue arises.
     */
    public int addEvent(EventBean event) throws SQLException {
        String query = "INSERT INTO event (client_id, name, description, start, duration, location, type, booking_type) VALUES (?,?,?,?,?,?, ?::event_type_enum, ?::booking_type_enum) RETURNING id";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, event.getClient_id());
        st.setString(2, event.getName());
        st.setString(3, event.getDescription());
        st.setTimestamp(4, event.getStart());
        st.setInt(5, event.getDuration());
        st.setString(6, event.getLocation());
        st.setString(7, event.getType().toString());
        st.setString(8, event.getBooking_type().toString());


        ResultSet rs = st.executeQuery();
        int event_id = -1;
        if (rs.next())
            event_id = rs.getInt(1);
        System.out.printf("===SQL=== ADDED AN EVENT TO DATABASE RETURNING ID %s\n", event_id);
        return event_id;
    }

    /**
     * Adds the crew required
     * @param required an requiredCrew object to define the appropriate relation of staff needed
     * @throws SQLException occurs when an SQL issue arises.
     */
    public void addRequirement(RequiredCrewBean required) throws SQLException {
        String query = "INSERT INTO event_requirement (event_id, crew_size, role) VALUES (?, ?, ?::role_enum)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, required.getEvent_id());
        st.setInt(2, required.getCrew_size());
        st.setString(3, required.getRole().toString());
        st.executeUpdate();
        System.out.printf("===SQL=== ADDED A ROLE REQUIREMENT WITH VALUES %s, %s, %s\n",
                required.getEvent_id(), required.getCrew_size(), required.getRole().toString());
    }

}

