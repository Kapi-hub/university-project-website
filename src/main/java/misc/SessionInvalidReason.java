package misc;

/**
 * An enum that holds the reasons why a session is invalid
 * EXPIRED - the session has expired
 * UNAUTHORIZED - the user is not authorized to access the requested resource
 * NOT_LOGGED_IN - the user is not logged in
 */
public enum SessionInvalidReason {
    EXPIRED,
    UNAUTHORIZED,
    NOT_LOGGED_IN
}
