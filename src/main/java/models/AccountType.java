package models;

/**
 * An enum that holds the different account types
 */
public enum AccountType {
    ADMIN,
    CREW_MEMBER,
    CLIENT;

    /**
     * Convert a string to an AccountType
     *
     * @param string the string to convert
     * @return the AccountType
     */
    public static AccountType toEnum(String string) {
        return string == null ? null : switch (string) {
            case "admin" -> ADMIN;
            case "crew_member" -> CREW_MEMBER;
            case "client" -> CLIENT;
            default -> null;
        };
    }

    /**
     * Convert an AccountType to a string by converting its name to lowercase
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}