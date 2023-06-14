package dao;

import misc.ConnectionFactory;
import models.AccountBean;
import models.AccountType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum AccountDao {
    instance;

    private final Connection connection;



    AccountDao() {
        connection = ConnectionFactory.getConnection();
    }

    public boolean checkValidLogin(AccountBean account) {

        try {
            String query = "SELECT id, type FROM account WHERE (username=? OR email_address=?) AND password=?";

            PreparedStatement st = connection.prepareStatement(query);

            st.setString(1, account.getUsername());
            st.setString(2, account.getUsername());
            st.setString(3, account.getPassword());

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                account.setId(rs.getInt(1));
                account.setAccountType(AccountType.toEnum(rs.getString(2)));
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("SQL Exception in checkValidLogin: " + e.getMessage());
            return false;
        }
    }

    public AccountType determineAccountType(int accountId) throws SQLException {
        String query = "SELECT type FROM account WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return AccountType.toEnum(rs.getString(1));
        }

        throw new SQLException("Account not found");
    }
}
