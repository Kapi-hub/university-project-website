package resources;

import dao.AccountDao;
import dao.EventDao;
import dao.SessionDao;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import misc.InvalidSessionException;
import misc.Security;
import misc.SessionInvalidReason;
import misc.SessionVerifier;
import models.AccountBean;
import models.AccountType;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;

@Path("/login")
public class LoginResource {
    // Map of account types to their corresponding paths they should be redirected to after a successful login
    private static final Map<AccountType, String> accountTypePaths = Map.of(
            AccountType.ADMIN, "admin/dashboard",
            AccountType.CREW_MEMBER, "crew/dashboard",
            // Unused, unless user accounts are implemented later
            AccountType.CLIENT, "submit"
    );

    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
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

        String sessionId = Security.generateSessionId();

        try {
            SessionDao.instance.putSessionId(account.getId(), sessionId);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating session")
                    .build();
        }

        NewCookie sessionIdCookie = createCookie("sessionId", sessionId);
        NewCookie accountIdCookie = createCookie("accountId", String.valueOf(account.getId()));

        URI uri = UriBuilder.fromPath("/")
                .path(accountTypePaths.get(account.getAccountType()))
                .build();

        String forename;
        try {
            forename = AccountDao.instance.getNames(account.getId())[0];
        } catch (SQLException e) {
            forename = "User";
        }
        int eventCount = EventDao.instance.getEventCount(account.getId(), account.getAccountType());

        return Response.ok()
                .location(uri)
                .header("forename", forename)
                .header("eventCount", eventCount)
                .cookie(accountIdCookie)
                .cookie(sessionIdCookie)
                .build();
    }

    @Path("/logout")
    @POST
    @PermitAll
    public Response handleLogout(@CookieParam("sessionId") String sessionId,
                                 @CookieParam("accountId") String accountIdString) {
        Response.ResponseBuilder response = Response.ok();
        for (int i = 0; i < 3; i++) {
            try {
                SessionVerifier.determineAccountType(sessionId, accountIdString);
            } catch (InvalidSessionException e) {
                if (e.getReason() != SessionInvalidReason.UNAUTHORIZED) {
                    // the user is either not logged already or trying to log someone else out by falsifying the cookies
                    // We therefore send the 200 response as this user is as a matter of fact logged out
                    return response.build();
                }
                // The unauthorized is only thrown if the users' session is valid but their account type could not be
                // determined. We still want to log this user out and delete their session id from the database
            }
            try {
                SessionDao.instance.deleteSessionId(Integer.parseInt(accountIdString), sessionId);
                return response.build();
            } catch (SQLException ignored) {
                // This means that the deletion failed, so we will try again.
            }
        }
        return Response.serverError()
                .build();
    }

    public NewCookie createCookie(String name, String value) {
        NewCookie.Builder cookieBuilder = new NewCookie.Builder(name);
        cookieBuilder.value(value);
        cookieBuilder.path("/");
        cookieBuilder.sameSite(NewCookie.SameSite.STRICT);
        return cookieBuilder.build();
    }
}
