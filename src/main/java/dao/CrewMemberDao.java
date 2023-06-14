package dao;

import misc.ConnectionFactory;
import models.RoleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


    public enum CrewMemberDao {
        I;

        final Connection connection;

        CrewMemberDao() {
            connection = ConnectionFactory.getConnection();
        }

        public RoleType getRole(int accountId) throws SQLException {
            String query = "SELECT role " +
                    "FROM crew_member " +
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

