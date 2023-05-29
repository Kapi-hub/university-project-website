package models;

public enum EventType {
    PHOTOGRAPHY,
    FILM,
    MARKETING,
    OTHER;

    @Override
    public String toString() {
        return this.name();
    }

}
