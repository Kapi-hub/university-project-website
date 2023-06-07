package models;

public enum EventType {
    CLUB_PHOTOGRAPHY,
    FESTIVAL,
    PHOTOSHOOT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
