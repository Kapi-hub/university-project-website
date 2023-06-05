import models.AccountBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.LoginResource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginResourceTest {

    LoginResource loginResource = new LoginResource();

    @BeforeEach
    public void setUp() {
        loginResource = new LoginResource();
    }

    @Test
    public void handleSubmitTest() {

        AccountBean adminAccount = new AccountBean("testAdmin", "testAdminPassword");
        AccountBean crewAccount = new AccountBean("testCrew", "testCrewPassword");
        AccountBean clientAccount; // ensure they can't log in yet

        LinkedHashMap<String, String> adminBeanData = new LinkedHashMap<>();
        LinkedHashMap<String, String> crewBeanData = new LinkedHashMap<>();

        adminBeanData.put("username", adminAccount.getUsername());
        crewBeanData.put("username", crewAccount.getUsername());

        adminBeanData.put("password", adminAccount.getPassword());
        crewBeanData.put("password", crewAccount.getPassword());

        Map<String, Object> adminFormData = new LinkedHashMap<>();
        Map<String, Object> crewFormData = new LinkedHashMap<>();

        adminFormData.put("accountBean", adminBeanData);
        crewFormData.put("accountBean", crewBeanData);

//        assertEquals(303, loginResource.handleSubmit(adminFormData).getStatus());
//        assertEquals(303, loginResource.handleSubmit(crewFormData).getStatus());
    }

}
