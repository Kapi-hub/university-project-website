package models;

public enum EventStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
