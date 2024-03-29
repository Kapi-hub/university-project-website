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

    /**
     * Handles the logging in with a specific account bean
     * @param account the account bean in question
     * @return true if the password in the account bean is, after digesting, equal otherwise false.
     */
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
            System.err.println("SQL Exception in checkValidLogin: " + e.getMessage());
            return false;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }
//    create or replace function checkValidLogin(username_or_email text)
//    returns setof account as $$
//            begin
//--first get the account
//    SELECT id, type, salt, password
//    FROM account
//    WHERE (username=username_or_email OR email_address=username_or_email)
//
//--second, set the
//    end;
//    $$ language plpgsql;

    /**
     * Determines account type based on the Account ID.
     * @param id the account id in question
     * @return the account type
     * @throws SQLException if SQL error occurs
     */
    public AccountType determineAccountType(int id) throws SQLException {
        String query = "SELECT type FROM account WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return AccountType.toEnum(rs.getString(1));
        }

        throw new SQLException("Account not found");
    }

    /**
     * Gets the forename and surname of an account given a specific client id
     * @param id the account id
     * @return the forename and surname
     */
    public String getName(int id) {
        if (id == 0) {
            return null;
            // returning null as this will be handled correctly by the JS on the client side.
        }
        try {
            String query = "SELECT forename, surname FROM account WHERE id = ?";

            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getString(1) + " " + rs.getString(2);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            // No error handling as next statement is return null anyway
        }

        return null;
    }

    /**
     * Gets the forename and surname of an account given a specific client id
     * @param accountId the account id
     * @return the forename and surname
     */
    public String[] getNames(int accountId) throws SQLException {
        String query = "SELECT forename, surname FROM account WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return new String[] {rs.getString(1), rs.getString(2)};
        }

        throw new SQLException("Account not found");
    }

    /**
     * Gets the forename and surname of an account given a specific client id
     * @param accountId the client id
     * @return the forename and surname in JSON format
     */
    public String getNamesAsJSON(int accountId) throws SQLException {
        String query = "SELECT json_agg(json_build_object('forename', forename, 'surname', surname))FROM account WHERE id = ?";

        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
//            return new String[] {rs.getString(1), rs.getString(2)};
            return rs.getString(1);
        }

        throw new SQLException("Account not found");
    }

    /**
     * Get email address by ID
     * @param accountId the account id in question
     * @return the email address of a specific account
     * @throws SQLException if sql error occurs.
     */
    public String getEmailAddressById(int accountId) throws SQLException {
        String query = "SELECT email_address FROM account WHERE id = ?";
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, accountId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return rs.getString(1);
        throw new SQLException("Account not found, so no email address found either.");}
}
