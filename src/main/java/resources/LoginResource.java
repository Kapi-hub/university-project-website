package resources;

import dao.AccountDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import models.AccountBean;
import models.AccountType;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/login")
public class LoginResource {

    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleSubmit(AccountBean account) {

        if(AccountDao.instance.checkLogin(account)) {

            NewCookie.Builder sessionIdCookieBuilder = new NewCookie.Builder("sessionId");
            sessionIdCookieBuilder.value(account.getSessionId());
            NewCookie sessionIdCookie = sessionIdCookieBuilder.build();

            NewCookie.Builder accountIdCookieBuilder = new NewCookie.Builder("accountId");
            accountIdCookieBuilder.value(String.valueOf(account.getAccountId()));
            NewCookie accountIdCookie = accountIdCookieBuilder.build();

            Response.ResponseBuilder responseBuilder;
            URI uri;

            if(account.getAccountType() == AccountType.ADMINISTRATOR) {
                // redirect to admin portal
                uri = URI.create("http://localhost:8080/shotmaniacs_war/pages/admin/mainPage/index.html");

            } else if (account.getAccountType() == AccountType.CREW_MEMBER) {
                // redirect to crew member portal
                uri = URI.create("http://localhost:8080/shotmaniacs_war/pages/crew/dashboard/index.html");

            } else if (account.getAccountType() == AccountType.CLIENT) {
                // redirect to form-submission for now
                uri = URI.create("http://localhost:8080/shotmaniacs_war/pages/client/index.html");

            } else {
                // handle unknown account type
                responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
                return responseBuilder.build();

            }
            responseBuilder = Response.seeOther(uri);

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