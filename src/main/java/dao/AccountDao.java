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

    final Connection connection;

    AccountDao() {
        connection = ConnectionFactory.getConnection();
    }

    public boolean checkLogin(AccountBean account) {

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
                account.setAccountType(determineAccountType(rs.getString(2)));

                String sessionId = sessionIdGenerator();

                putSessionId(accountId, sessionId);

                account.setSessionId(sessionId);
                return true;

            }
            return false;

        } catch (SQLException e) {
            System.out.println("SQL Exception in checkLogin: " + e.getMessage());
            return false;
        }
    }

    public boolean checkSession(AccountBean account) {

        try {
            String query = "SELECT a.type FROM account a, has_login_session s WHERE s.account_id=? AND s.session_id=? " +
                    "AND a.id = s.account_id AND s.expires > NOW()";

            PreparedStatement st = connection.prepareStatement(query);

            st.setInt(1, account.getAccountId());
            st.setString(2, account.getSessionId());

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                account.setAccountType(determineAccountType(rs.getString(1)));
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("SQL Exception in checkSession: " + e.getMessage());
            return false;
        }
    }

    private String sessionIdGenerator() {
        String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()[]{}_+:.<>?|~-";

        StringBuilder sessionId = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            int index = (int) (possibleChars.length() * Math.random());
            sessionId.append(possibleChars.charAt(index));
        }

        return sessionId.toString();
    }

    private AccountType determineAccountType(String accountType) {

        return switch (accountType) {
            case "administrator" -> AccountType.ADMINISTRATOR;
            case "crew_member" -> AccountType.CREW_MEMBER;
            case "client" -> AccountType.CLIENT;
            default -> null;
        };
    }

    private void putSessionId(int accountId, String sessionId) throws SQLException {
        String query = "UPDATE has_login_session SET session_id = ? WHERE account_id = ?; " +
                "INSERT INTO has_login_session (account_id, session_id) SELECT ?, ? " +
                "WHERE NOT EXISTS (SELECT 1 FROM has_login_session WHERE account_id = ?);";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, sessionId);
        st.setInt(2, accountId);
        st.setInt(3, accountId);
        st.setString(4, sessionId);
        st.setInt(5, accountId);

        st.executeUpdate();
    }

}
