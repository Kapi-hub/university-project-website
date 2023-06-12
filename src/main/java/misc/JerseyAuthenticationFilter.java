package misc;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import models.AccountType;

import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JerseyAuthenticationFilter implements ContainerRequestFilter {

    @Context
    private HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("Someone is trying to access a resource");
        String sessionId = headers.getCookies().get("sessionId").getValue();
        String accountIdString = headers.getCookies().get("accountId").getValue();

        AccountType accountType;

        try {
            accountType = SessionVerifier.determineAccountType(sessionId, accountIdString);
        } catch (InvalidSessionException e) {
            accountType = AccountType.CLIENT;
        }

        AccountType finalAccountType = accountType;
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return finalAccountType::toString;
            }

            @Override
            public boolean isUserInRole(String role) {
                return finalAccountType.toString().equals(role);
            }

            @Override
            public boolean isSecure() {
                return requestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        });

    }
}
