package models;

public enum Team {
    CORE,
    CLUB,
    CORE_AND_CLUB;

    @Override
    public String toString() {
        return this.name();
    }
}
