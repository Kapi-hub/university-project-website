package misc;

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
