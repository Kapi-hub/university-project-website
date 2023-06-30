package dao;

import misc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to handle connectivity with the database related to events
 */
public enum SessionDao {

    instance;

    private final Connection connection;

    /**
     * Sets up connection with the database
     */
    SessionDao() {
        connection = ConnectionFactory.getConnection();
    }

    /**
     * Checks whether the account has a valid session.
     * @param accountId the account in question
     * @param sessionId the sessionID that has to be checked.
     * @return true if successful otherwise false
     */
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

    /**
     * Inserts the session ID in the database
     * @param accountId accountID in question
     * @param sessionId the generated session ID
     * @throws SQLException when SQL error occurs
     */
    public void putSessionId(int accountId, String sessionId) throws SQLException {
        String insertQuery = "INSERT INTO has_login_session (account_id, session_id) VALUES (?, ?)";

        PreparedStatement insertSt = connection.prepareStatement(insertQuery);

        insertSt.setInt(1, accountId);
        insertSt.setString(2, sessionId);
        insertSt.executeUpdate();
    }

    /**
     * Deletes sessionID, for instance when logging out
     * @param accountId the session ID in question.
     * @param sessionId the session ID is needed, since a person might have multiple session IDs.
     * @throws SQLException when SQL error occurs.
     */
    public void deleteSessionId(int accountId, String sessionId) throws SQLException {
        String query = "DELETE FROM has_login_session WHERE account_id = ? AND session_id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);
        st.setString(2, sessionId);

        st.executeUpdate();
    }
}
