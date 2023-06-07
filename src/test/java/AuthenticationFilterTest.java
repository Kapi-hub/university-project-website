import misc.AuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationFilterTest {

    private AuthenticationFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new AuthenticationFilter();
    }

    @Test
    public void isValidSessionTest() {
        String adminSessionId = "adminadminadminadmin";
        String crewSessionId = "crewcrewcrewcrewcrew";
        String clientSessionId = ""; // should not be a thing yet, so ensure it really isn't!
        String bsSessionId = "bsSessionId";
        String nullSessionId = null;

        String adminAccountId = "7";
        String crewAccountId = "8";
        String clientAccountId = "9";
        String bsAccountId = "12345";
        String nullAccountId = null;

        String adminPath = "/shotmaniacs_war/pages/admin/mainPage/index.html";
        String crewPath = "/shotmaniacs_war/pages/crew/dashboard/index.html";
        String clientPath = ""; // should not even call this method, though maybe think about equipping it to handle
        // this sort of edge-case??

        String[] allSessionIds = {adminSessionId, crewSessionId, bsSessionId, nullSessionId};
        String[] allAccountIds = {adminAccountId, crewAccountId, clientAccountId, bsAccountId, nullAccountId};
        String[] allPaths = {adminPath, crewPath};

        ArrayList<String[]> validCombos = new ArrayList<>();
        String[] adminCombo = {adminSessionId, adminAccountId, adminPath};
        String[] crewCombo = {crewSessionId, crewAccountId, crewPath};
        validCombos.add(adminCombo);
        validCombos.add(crewCombo);

        for (String sessionId : allSessionIds) {

            for (String accountId : allAccountIds) {

                for (String path : allPaths) {
                    boolean isValidCombo = false;
                    String[] currentCombo = {sessionId, accountId, path};

                    // check if current combo is in validCombos
                    for (String[] combo : validCombos) {
                        if (Arrays.equals(currentCombo, combo)) {
                            isValidCombo = true;
                            break;
                        }
                    }

                    if (isValidCombo) {
                        System.out.println("Testing valid combo: " + Arrays.toString(currentCombo));
//                        assertTrue(filter.isValidSession(sessionId, accountId, path));
                    } else {
                        System.out.println("Testing invalid combo: " + Arrays.toString(currentCombo));
//                        assertFalse(filter.isValidSession(sessionId, accountId, path));
                    }

                }

            }

        }

    }
}
