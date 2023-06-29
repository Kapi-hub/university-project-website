package login;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import static misc.Security.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestHash {
    @Test
    public void testEncodeSalt() throws GeneralSecurityException {
        String password = "password";

        String[] encoded = encodeSalt(password);
        assertTrue(64 <= encoded[0].length());
        assertTrue(16 <= encoded[1].length());
    }

    @Test
    public void testDecodeSalt() throws GeneralSecurityException {
        String encodedHashPassword = "Su3dGn0ryG5X4YMyY0c3tr/tf/iFRoJUnCVQ+IEvdQ7OuEmhWBu3XI+ItOqPDm7CmsMBWxorNrHZbKdrKSrPQw==";
        String encodedSalt = "LiZf26NDdSm48TwRWIKh9w==";
        String password = "password";
        String wrong_password = "wrong_password";
        assertTrue(checkPassword(password, encodedSalt, encodedHashPassword));
        assertFalse(checkPassword(wrong_password, encodedSalt, encodedHashPassword));
    }

    @Test
    public void testGenerateSessionID() {
        assertTrue(16 <= generateSessionId().length());
    }
}
