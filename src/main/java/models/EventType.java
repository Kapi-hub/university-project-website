package models;

public enum EventType {
    CLUB_PHOTOGRAPHY,
    FESTIVAL,
    PHOTOSHOOT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static EventType toEnum(String string) {
        return switch(string.toLowerCase()) {
            case "club_photography" -> CLUB_PHOTOGRAPHY;
            case "festival" -> FESTIVAL;
            case "photoshoot" -> PHOTOSHOOT;
            default -> null;
        };
    }
}
