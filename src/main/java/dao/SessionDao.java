package dao;

import misc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum SessionDao {

    instance;

    private final Connection connection;

    SessionDao() {
        connection = ConnectionFactory.getConnection();
    }

    public boolean checkValidSession(int accountId, String sessionId) {
        try {
            String query = "SELECT 1 FROM has_login_session " +
                    "WHERE account_id=? AND session_id=? AND expires > NOW()";

            PreparedStatement st = connection.prepareStatement(query);

            st.setInt(1, accountId);
            st.setString(2, sessionId);

            ResultSet rs = st.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.err.println("SQL Exception in checkValidSession: " + e.getMessage());
            return false;
        }
    }

    public void putSessionId(int accountId, String sessionId) throws SQLException {
        String insertQuery = "INSERT INTO has_login_session (account_id, session_id) VALUES (?, ?)";

        PreparedStatement insertSt = connection.prepareStatement(insertQuery);

        insertSt.setInt(1, accountId);
        insertSt.setString(2, sessionId);
        insertSt.executeUpdate();
    }

    public void deleteSessionId(int accountId, String sessionId) throws SQLException {
        String query = "DELETE FROM has_login_session WHERE account_id = ? AND session_id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);
        st.setString(2, sessionId);

        st.executeUpdate();
    }
}
