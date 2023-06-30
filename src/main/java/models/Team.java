package models;

/**
 * An enum that holds the different teams crew members can be a part of
 */
public enum Team {
    CORE,
    CLUB,
    CORE_CLUB;

    /**
     * Convert the Team to a string by converting its name to lowercase
     * @return the string
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
