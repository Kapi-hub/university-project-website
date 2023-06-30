package models;

/**
 * An enum that holds the different booking types of an event
 */
public enum BookingType {
    PHOTOGRAPHY,
    FILM,
    MARKETING,
    OTHER;

    /**
     * Convert a string holding the name of the booking type to a BookingType, regardless of case
     *
     * @param s the string to convert
     * @return the BookingType
     */
    public static BookingType toEnum(String s) {
        return s == null ? null : switch (s.toLowerCase()) {
            case "photography" -> PHOTOGRAPHY;
            case "film" -> FILM;
            case "marketing" -> MARKETING;
            case "other" -> OTHER;
            default -> null;
        };
    }

    /**
     * Convert a BookingType to a string by converting its name to lowercase
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
