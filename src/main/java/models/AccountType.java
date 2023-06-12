package models;

public enum AccountType {
    ADMIN,
    CREW_MEMBER,
    CLIENT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static AccountType toEnum(String string) {
        return switch (string) {
            case "admin" -> ADMIN;
            case "crew_member" -> CREW_MEMBER;
            case "client" -> CLIENT;
            default -> null;
        };
    }

}