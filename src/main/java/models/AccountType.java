package models;

public enum AccountType {
    ADMIN,
    CREW_MEMBER,
    CLIENT;

    public static AccountType toEnum(String string) {
        return string == null ? null : switch (string) {
            case "admin" -> ADMIN;
            case "crew_member" -> CREW_MEMBER;
            case "client" -> CLIENT;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}