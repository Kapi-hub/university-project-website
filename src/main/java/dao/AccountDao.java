package dao;

import misc.ConnectionFactory;
import misc.Security;
import models.AccountBean;
import models.AccountType;

import java.security.GeneralSecurityException;
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
            String query = "SELECT id, type, salt, password FROM account WHERE (username=? OR email_address=?)";

            PreparedStatement st = connection.prepareStatement(query);

            st.setString(1, account.getUsername());
            st.setString(2, account.getUsername());
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                account.setId(rs.getInt(1));
                account.setAccountType(AccountType.toEnum(rs.getString(2)));
                return Security.checkPassword(account.getPassword(), rs.getString(3), rs.getString(4));
            }
            return false;

        } catch (SQLException e) {
            System.out.println("SQL Exception in checkValidLogin: " + e.getMessage());
            return false;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
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
