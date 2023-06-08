package dao;

import misc.ConnectionFactory;
import models.AccountBean;
import models.AccountType;
import models.SessionBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public enum AccountDao {
    instance;

    // Map of account types to their corresponding enum
    private static final Map<String, AccountType> ACCOUNT_TYPE_MAP = Map.of(
            "administrator", AccountType.ADMINISTRATOR,
            "crew member", AccountType.CREW_MEMBER,
            "client", AccountType.CLIENT
    );

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

                int accountId = rs.getInt(1);
                account.setAccountId(accountId);
                account.setAccountType(getAccountTypeEnum(rs.getString(2)));

                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("SQL Exception in checkValidLogin: " + e.getMessage());
            return false;
        }
    }

    public AccountType determineAccountType(int AccountId) throws SQLException {
        String query = "SELECT type FROM account WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, AccountId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return getAccountTypeEnum(rs.getString(1));
        }

        throw new SQLException("Account not found");
    }

    public AccountType getAccountTypeEnum(String accountType) {
        return ACCOUNT_TYPE_MAP.get(accountType);
    }
}
