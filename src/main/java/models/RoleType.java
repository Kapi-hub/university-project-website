package models;

public enum RoleType {
    ASSISTANT,
    DATA_HANDLER,
    EDITOR,
    PHOTOGRAPHER,
    PLANNER,
    PRODUCER,
    VIDEOGRAPHER;

    public static RoleType toEum(String roleString) {
        return switch (roleString) {
            case "assistant" -> ASSISTANT;
            case "data_handler" -> DATA_HANDLER;
            case "editor" -> EDITOR;
            case "photographer" -> PHOTOGRAPHER;
            case "planner" -> PLANNER;
            case "producer" -> PRODUCER;
            case "videographer" -> VIDEOGRAPHER;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
