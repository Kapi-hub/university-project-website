package misc;

import java.util.Random;

public class UltraAdmin {

    private static final String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789!@#$%^&*()[]{}_+:.<>?|~-";
    private static String adminKey;

    public static void setup() {
        Random random = new Random();
        StringBuilder adminKeyBuilder = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            int index = random.nextInt(possibleChars.length());
            adminKeyBuilder.append(possibleChars.charAt(index));
        }
        adminKey = System.getenv("ULTRA_PASSWORD");
        if (adminKey == null) {
            adminKey = adminKeyBuilder.toString();
        }
        String green = "\u001B[32m";
        String reset = "\u001B[0m";
        System.out.println("\n\n\n" + green + "---ULTRA-MEGA-KEY---" + reset);
        System.out.println(adminKey);
        System.out.println(green + "---ULTRA-MEGA-KEY---" + reset + "\n\n\n");
    }

    public static boolean isAdmin(String key) {
        return key.equals(adminKey);
    }

    public static String getAdminKey() {
        return adminKey;
    }

    public static void setAdminKey(String key) {
        adminKey = key;
    }
}
