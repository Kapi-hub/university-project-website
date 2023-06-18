package resources;

import dao.MailService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import misc.ConnectionFactory;
import misc.UltraAdmin;
import models.UltraAdminBean;
import models.UltraDbBean;
import models.UltraEmailBean;

@Path("/ultra-admin")
public class UltraAdminResource {

    @Path("/set-id-and-secret")
    @PermitAll
    public Response setIdAndSecret(UltraEmailBean emailBean) {
        String adminKey = emailBean.getAdminKey();
        if (isNotUltraAdmin(adminKey)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        String mailClientId = emailBean.getMailClientId();
        String mailClientSecret = emailBean.getMailClientSecret();
        MailService.MAIL.printAuthURL(mailClientId, mailClientSecret);
        return Response.ok().build();
    }

    @Path("/activate-email")
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
