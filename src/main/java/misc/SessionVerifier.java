package misc;

import dao.AccountDao;
import dao.SessionDao;
import models.AccountType;

import java.sql.SQLException;


/**
 * This class is used to verify that a session is valid.
 * It has only one public method, determineAccountType, which takes a session ID and an account ID.
 * It returns the account type of the user if the session is valid, and throws an InvalidSessionException otherwise.
 **/
public class SessionVerifier {
    public static AccountType determineAccountType(String sessionId, String accountIdString) throws InvalidSessionException {
        // catch invalid session, for example if the user is not logged in
        if (sessionId == null || accountIdString == null) {
            throw new InvalidSessionException(SessionInvalidReason.NOT_LOGGED_IN);
        }

        int accountId;
        try {
            accountId = Integer.parseInt(accountIdString);
        } catch (NumberFormatException e) {
            // handle invalid account ID
            throw new InvalidSessionException(SessionInvalidReason.NOT_LOGGED_IN);
        }

        boolean sessionIsValid = SessionDao.instance.checkValidSession(accountId, sessionId);

        // and invalid session at this point means that the session likely has expired
        if (!sessionIsValid) {
            throw new InvalidSessionException(SessionInvalidReason.EXPIRED);
        }

        AccountType accountType;

        try {
            accountType = AccountDao.instance.determineAccountType(accountId);
        } catch (SQLException e) {
            // we were unable to determine the users account type:
            // to be safe, it has to be assumed the user is not authorized to do anything
            throw new InvalidSessionException(SessionInvalidReason.UNAUTHORIZED);
        }

        return accountType;
    }
}
