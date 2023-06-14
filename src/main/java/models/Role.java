package models;

public enum Role {
    ASSISTANT,
    DATA_HANDLER,
    EDITOR,
    PHOTOGRAPHER,
    PLANNER,
    PRODUCER,
    VIDEOGRAPHER;

    @Override
    public String toString() {
        return this.name();
    }
}
