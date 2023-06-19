package resources;

import dao.MailService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import misc.UltraAdmin;
import models.UltraAdminBean;
import models.UltraDbBean;
import models.UltraEmailBean;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Path("/ultra-admin")
public class UltraAdminResource {

    @Path("/set-id-and-secret")
    @Consumes("application/json")
    @POST
    @PermitAll
    public Response setIdAndSecret(UltraEmailBean emailBean) {
        String adminKey = emailBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        String mailClientId = emailBean.getMailClientId();
        String mailClientSecret = emailBean.getMailClientSecret();
        URI authURL;
        try {
            authURL = new URI(Objects.requireNonNull(MailService.MAIL.printAuthURL(mailClientId, mailClientSecret)));
        } catch (URISyntaxException | NullPointerException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
        return Response.ok()
                .header("Location", authURL)
                .build();
    }

    @Path("/activate-email")
    @Consumes("application/json")
    @POST
    @PermitAll
    public Response activateEmail(UltraEmailBean emailBean) {
        String adminKey = emailBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        String emailToken = emailBean.getEmailToken();
        MailService.MAIL.setToken(emailToken);
        MailService.MAIL.connect();
        return Response.ok().build();
    }

    @Path("/disable-email")
    @Consumes("application/json")
    @POST
    @PermitAll
    public Response disableEmail(UltraAdminBean adminBean) {
        String adminKey = adminBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        MailService.MAIL.deactivate();
        return Response.ok().build();
    }

    @Path("/reconnect-mail")
    @Consumes("application/json")
    @POST
    @PermitAll
    public Response reconnectMail(UltraAdminBean adminBean) {
        String adminKey = adminBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        MailService.MAIL.connect();
        return Response.ok().build();
    }

    @Path("/change-db")
    @Consumes("application/json")
    @POST
    @PermitAll
    public Response changeDb(UltraDbBean dbBean) {
        String adminKey = dbBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        String dbPassword = dbBean.getDbPassword();
        Boolean previderBool = dbBean.getPreviderBool();
        ConnectionFactory.setDbPassword(dbPassword);
        ConnectionFactory.setPreviderBool(previderBool);
        ConnectionFactory.reconnect();
        return Response.ok().build();
    }

    @Path("/reconnect-db")
    @Consumes("application/json")
    @POST
    @PermitAll
    public Response reconnectDb(UltraAdminBean adminBean) {
        String adminKey = adminBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        ConnectionFactory.reconnect();
        return Response.ok().build();
    }

    private boolean isNotUltraAdmin(String adminKey) {
        return !UltraAdmin.isAdmin(adminKey);
    }
}
