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

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JerseyAuthenticationFilter implements ContainerRequestFilter {

    @Context
    private HttpHeaders headers;

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
