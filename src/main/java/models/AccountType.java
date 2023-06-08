package models;

public enum AccountType {
    ADMINISTRATOR,
    CREW_MEMBER,
    CLIENT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static AccountType toEnum(String string) {
        return switch (string) {
            case "administrator" -> ADMINISTRATOR;
            case "crew_member" -> CREW_MEMBER;
            case "client" -> CLIENT;
            default -> null;
        };
    }

}