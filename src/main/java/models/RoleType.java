package models;

/**
 * An enum that holds the different crew roles
 */
public enum RoleType {
    ASSISTANT,
    DATA_HANDLER,
    EDITOR,
    PHOTOGRAPHER,
    PLANNER,
    PRODUCER,
    VIDEOGRAPHER;

    /**
     * Convert a string holding the name of the crew role to a RoleType, regardless of case
     *
     * @param string the string to convert
     * @return the RoleType
     */
    public static RoleType toEum(String string) {
        return string == null ? null : switch (string) {
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

    /**
     * Convert a RoleType to a string by converting its name to lowercase
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
