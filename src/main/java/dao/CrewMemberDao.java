package dao;

import misc.ConnectionFactory;
import models.RoleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Class to handle connectivity with the database related to announcements
 */
public enum CrewMemberDao {
    I;

    final Connection connection;

    /**
     * Setting up the connection
     */
    CrewMemberDao() {
        connection = ConnectionFactory.getConnection();
    }

    /**
     * Get the role type
     * @param accountId the role of a specific accountID
     * @return the role
     * @throws SQLException occurs when SQL error
     */
    public RoleType getRole(int accountId) throws SQLException {
        String query = "SELECT role "+
                "FROM crew_member "+
                "WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return RoleType.toEum(rs.getString(1));
        }

        throw new SQLException("Role not found");
    }

}

