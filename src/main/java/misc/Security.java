package misc;

import com.lambdaworks.crypto.SCrypt;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

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

    public static boolean checkPassword(String password, String encodedSalt, String encodedPassword) throws GeneralSecurityException {
        byte[] storedHashedPassword = Base64.getDecoder().decode(encodedPassword);
        byte[] storedSalt = Base64.getDecoder().decode(encodedSalt);

        byte[] inputHashedPassword = SCrypt.scrypt(password.getBytes(), storedSalt, N, r, p, dkLen);

        return MessageDigest.isEqual(storedHashedPassword, inputHashedPassword);
    }

    private static final int SESSION_ID_LENGTH = 16;

    public static String generateSessionId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[SESSION_ID_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
