package models;

public enum EventStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE;

    public static EventStatus toEnum(String s) {
        return s == null ? null : switch (s) {
            case "to do" -> TODO;
            case "in_progress" -> IN_PROGRESS;
            case "review" -> REVIEW;
            case "done" -> DONE;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
