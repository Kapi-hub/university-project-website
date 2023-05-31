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

    public static void main(String[] args) throws SQLException {
        EventBean event = new EventBean(-1, "De Reactie", EventType.PHOTOGRAPHY, new Date(System.currentTimeMillis()), new Time(System.currentTimeMillis()), 10, "Evenemenenten Terein Universiteit", -1, 20, -1);
        ClientBean client = new ClientBean("Organisatie", "De Reactie", "organisatie@gmail.com", "06123123123");
        int id = I.addClient(client);
        event.setClient_id(id);
        I.addEvent(event);
    }

    public int addClient(ClientBean client) throws SQLException {
        String query = "INSERT INTO client (client_id, first_name, last_name, email, phone_number) VALUES (DEFAULT, ?,?,?,?) RETURNING client_id";
        PreparedStatement st = connection.prepareStatement(query);
        String[] values = {client.getFirst_name(), client.getLast_name(), client.getEmail(), client.getPhone_number()};
        for (int i = 1; i <= values.length; i++) {
            st.setString(i, values[i-1]);
        }
        ResultSet rs = st.executeQuery();
        System.out.println("===SQL=== ADDED CLIENT TO DATABASE");
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    public void addEvent(EventBean event) throws SQLException {
        String query = "INSERT INTO event (event_id, client_id, name, type, date, start_time, duration, location, production_manager_id, crew_members, status) "+
                "VALUES (DEFAULT, ?, ?, ?::event_type, ?, ?, ?, ?, DEFAULT, ?, DEFAULT)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, event.getClient_id());
        st.setString(2, event.getName());
        st.setString(3, event.getType().toString());
        st.setDate(4, event.getDate());
        st.setTime(5, event.getStart_time());
        st.setInt(6, event.getDuration());
        st.setString(7, event.getLocation());
        st.setInt(8, event.getCrew_members());
        st.executeUpdate();
        System.out.println("===SQL=== ADDED EVENT TO DATABASE");
    }
}

