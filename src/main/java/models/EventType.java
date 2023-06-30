package models;

/**
 * Different type of events in an enum.
 */
public enum EventType {
    CLUB_PHOTOGRAPHY,
    FESTIVAL,
    PRODUCT_SHOOT,
    OTHER;

    /**
     * Convert the EventType to a string by converting its name to lowercase
     * @return
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * Convert a string holding the name of the event type to an EventType, regardless of case
     * @param string the string to convert
     * @return the EventType
     */
    public static EventType toEnum(String string) {
        return string == null ? null : switch(string.toLowerCase()) {
            case "club_photography" -> CLUB_PHOTOGRAPHY;
            case "festival" -> FESTIVAL;
            case "product_shoot" -> PRODUCT_SHOOT;
            default -> null;
        };
    }
}
