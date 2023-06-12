package resources;

import dao.AccountDao;
import dao.SessionDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import models.AccountBean;
import models.AccountType;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

@Path("/login")
public class LoginResource {
    // Map of account types to their corresponding paths they should be redirected to after a successful login
    private static final Map<AccountType, String> accountTypePaths = Map.of(
            AccountType.ADMIN, "admin/dashboard",
            AccountType.CREW_MEMBER, "crew/dashboard",
            // Unused, unless user accounts are implemented later
            AccountType.CLIENT, "submit"
    );
    // All possible characters for the sessionId generator
    private static final String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789!@#$%^&*()[]{}_+:.<>?|~-";

    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleSubmit(AccountBean account) {

        if (account == null || account.getUsername() == null || account.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid account information")
                    .build();
        }

        if (!AccountDao.instance.checkValidLogin(account)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Wrong username/email or password")
                    .build();
        }

        String sessionId = sessionIdGenerator();

        try {
            SessionDao.instance.putSessionId(account.getAccountId(), sessionId);
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating session")
                    .build();
        }

        NewCookie sessionIdCookie = createCookie("sessionId", sessionId);
        NewCookie accountIdCookie = createCookie("accountId", String.valueOf(account.getAccountId()));

        URI uri = UriBuilder.fromPath("/")
                .path(accountTypePaths.get(account.getAccountType()))
                .build();

        return Response.ok()
                .location(uri)
                .cookie(accountIdCookie)
                .cookie(sessionIdCookie)
                .build();
    }

    public NewCookie createCookie(String name, String value) {
        NewCookie.Builder cookieBuilder = new NewCookie.Builder(name);
        cookieBuilder.value(value);
        cookieBuilder.path("/");
        cookieBuilder.sameSite(NewCookie.SameSite.STRICT);
        return cookieBuilder.build();
    }

    public String sessionIdGenerator() {
        Random random = new Random();
        StringBuilder sessionId = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            int index = random.nextInt(possibleChars.length());
            sessionId.append(possibleChars.charAt(index));
        }
        return sessionId.toString();
    }
}
