package resources;

import dao.AccountDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import models.AccountBean;
import models.AccountType;

import java.net.URI;

@Path("/login")
public class LoginResource {

    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleSubmit(AccountBean account) {

        if (AccountDao.instance.checkLogin(account)) {

            NewCookie.Builder sessionIdCookieBuilder = new NewCookie.Builder("sessionId");
            sessionIdCookieBuilder.value(account.getSessionId());
            sessionIdCookieBuilder.path("/");
            sessionIdCookieBuilder.sameSite(NewCookie.SameSite.STRICT);
            NewCookie sessionIdCookie = sessionIdCookieBuilder.build();

            NewCookie.Builder accountIdCookieBuilder = new NewCookie.Builder("accountId");
            accountIdCookieBuilder.value(String.valueOf(account.getAccountId()));
            accountIdCookieBuilder.path("/");
            accountIdCookieBuilder.sameSite(NewCookie.SameSite.STRICT);
            NewCookie accountIdCookie = accountIdCookieBuilder.build();

            Response.ResponseBuilder responseBuilder;
            URI uri;

            if (account.getAccountType() == AccountType.ADMINISTRATOR) {
                // redirect to admin portal
                uri = URI.create("/pages/admin/mainPage/index.html");

            } else if (account.getAccountType() == AccountType.CREW_MEMBER) {
                // redirect to crew member portal
                uri = URI.create("/pages/crew/dashboard/index.html");

            } else if (account.getAccountType() == AccountType.CLIENT) {
                // redirect to form-submission for now
                uri = URI.create("/pages/client/index.html");

            } else {
                // handle unknown account type
                responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
                return responseBuilder.build();

            }
            responseBuilder = Response.ok();
            responseBuilder.location(uri);

            responseBuilder.cookie(accountIdCookie);
            responseBuilder.cookie(sessionIdCookie);

            return responseBuilder.build();

        } else {
            // handle wrong password/account identifier
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.UNAUTHORIZED);
            responseBuilder.entity("Wrong username/email or password");
            return responseBuilder.build();
        }
    }
}
