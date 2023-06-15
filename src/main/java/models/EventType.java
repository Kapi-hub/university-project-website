package models;

public enum EventType {
    CLUB_PHOTOGRAPHY,
    FESTIVAL,
    PRODUCT_SHOOT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static EventType toEnum(String string) {
        return string == null ? null : switch(string.toLowerCase()) {
            case "club_photography" -> CLUB_PHOTOGRAPHY;
            case "festival" -> FESTIVAL;
            case "product_shoot" -> PRODUCT_SHOOT;
            default -> null;
        };
    }
}
