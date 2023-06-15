package models;

public enum BookingType {
    PHOTOGRAPHY,
    FILM,
    MARKETING,
    OTHER;

    public static BookingType toEnum(String s) {
        return s == null ? null : switch (s.toLowerCase()) {
            case "photography" -> PHOTOGRAPHY;
            case "film" -> FILM;
            case "marketing" -> MARKETING;
            case "other" -> OTHER;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
