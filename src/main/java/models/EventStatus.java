package models;

/**
 * An enum that holds the different event statuses
 */
public enum EventStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE;

    /**
     * Convert a string holding the name of the event status to an EventStatus, regardless of case
     *
     * @param s the string to convert
     * @return the EventStatus
     */
    public static EventStatus toEnum(String s) {
        return s == null ? null : switch (s) {
            case "to do" -> TODO;
            case "in_progress" -> IN_PROGRESS;
            case "review" -> REVIEW;
            case "done" -> DONE;
            default -> null;
        };
    }

    /**
     * Convert an EventStatus to a string by converting its name to lowercase
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
