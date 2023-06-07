package models;

public enum AccountType {
    ADMINISTRATOR,
    CREW_MEMBER,
    CLIENT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}