package misc;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import models.AccountType;

import java.io.IOException;
import java.security.Principal;

/**
 * Class to handle the authentication of requests to the api
 * It uses the Jax-RS ContainerRequestFilter interface to intercept requests
 * It is annotated with @Provider to register it as a provider
 * It is annotated with @Priority to give it a higher priority than the other filters
 * It is needed for the @RolesAllowed annotation
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JerseyAuthenticationFilter implements ContainerRequestFilter {

    @Context
    private HttpHeaders headers;

    /**
     * Method to handle the authentication of requests to the api
     * It checks the sessionId and accountId cookies to determine the account type of the user
     * If the sessionId is invalid, the user is treated as a client
     * This account type is then stored in the request context of the request
     * @param requestContext the request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Cookie sessionIdCookie = headers.getCookies() != null ? headers.getCookies().get("sessionId") : null;
        Cookie accountIdCookie = headers.getCookies() != null ? headers.getCookies().get("accountId") : null;
        String sessionId = null;
        String accountIdString = null;
        if (sessionIdCookie != null && accountIdCookie != null) {
            sessionId = sessionIdCookie.getValue();
            accountIdString = accountIdCookie.getValue();
        }
        AccountType accountType;

        try {
            accountType = SessionVerifier.determineAccountType(sessionId, accountIdString);
        } catch (InvalidSessionException e) {
            accountType = AccountType.CLIENT;
        }

        requestContext.setSecurityContext(new SecurityContextImpl(accountType));
    }

    static class SecurityContextImpl implements SecurityContext {

        private final AccountType accountType;

        public SecurityContextImpl(AccountType accountType) {
            this.accountType = accountType;
        }

        @Override
        public Principal getUserPrincipal() {
            return accountType::toString;
        }

        @Override
        public boolean isUserInRole(String role) {
            return role.equals(accountType.toString());
        }

        @Override
        public boolean isSecure() {
            return true;
        }

        @Override
        public String getAuthenticationScheme() {
            return null;
        }
    }
}
