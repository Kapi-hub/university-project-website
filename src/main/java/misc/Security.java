package misc;

import com.lambdaworks.crypto.SCrypt;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

/**
 * A security class for the project, including cryptography.
 */
public class Security {
    private static final int N = 16384; // CPU/memory cost parameter
    private static final int r = 8; // block size parameter
    private static final int p = 1; // parallelization parameter
    private static final int dkLen = 64; // derived key length

    /**
     * @param password to be hashed
     * @return[0] Base64 encoded hashedPassword
     * @return[1] Base64 encoded salt
     * @throws GeneralSecurityException
     */
    public static String[] encodeSalt(String password) throws GeneralSecurityException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);


        byte[] hashedPassword = SCrypt.scrypt(password.getBytes(), salt, N, r, p, dkLen);

        String encodedHashedPassword = Base64.getEncoder().encodeToString(hashedPassword);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        return new String[]{encodedHashedPassword, encodedSalt};
    }

    /**
     * Checks the password for a given set of inputs
     * @param password the password it needs to check
     * @param encodedSalt the salt normally stored on the database
     * @param encodedPassword the encoded password stored on the database
     * @return true if the password match
     * @throws GeneralSecurityException when an issue occurs with the cryptography.
     */
    public static boolean checkPassword(String password, String encodedSalt, String encodedPassword) throws GeneralSecurityException {
        try {
            byte[] storedHashedPassword = Base64.getDecoder().decode(encodedPassword);
            byte[] storedSalt = Base64.getDecoder().decode(encodedSalt);

            byte[] inputHashedPassword = SCrypt.scrypt(password.getBytes(), storedSalt, N, r, p, dkLen);

            return MessageDigest.isEqual(storedHashedPassword, inputHashedPassword);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static final int SESSION_ID_LENGTH = 16;

    /**
     * A secure way of generation session ID's
     * @return the generated session ID
     */
    public static String generateSessionId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[SESSION_ID_LENGTH];
        secureRandom.nextBytes(randomBytes);
        // return Base64 url encoded string of random bytes with same length of 16
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    @Deprecated
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter password to get random salt and encoded password: ");
        String submission = sc.nextLine();

        String[] retVal;
        try {
            retVal = encodeSalt(submission);
        } catch (GeneralSecurityException e) {
            System.out.println("Something went wrong, please try again");
            System.exit(0);
            return;
        }
        System.out.println("Encoded password:\n" + retVal[0]);
        System.out.println("\nCorresponding salt:\n" + retVal[1]);
    }
}
