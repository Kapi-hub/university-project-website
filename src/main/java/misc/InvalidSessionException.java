package misc;

/**
 * An exception that is thrown when the session is invalid
 * It holds the reason why the session is invalid
 */
public class InvalidSessionException extends Exception {
    private final SessionInvalidReason reason;

    public InvalidSessionException(SessionInvalidReason reason) {
        super(reason.toString());
        this.reason = reason;
    }

    public SessionInvalidReason getReason() {
        return reason;
    }
}
