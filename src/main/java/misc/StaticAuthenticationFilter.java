package misc;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.AccountType;

import java.io.IOException;

/**
 * Class to handle the authentication of requests to the static pages
 * It uses the Servlet Filter interface to intercept requests
 */
public class StaticAuthenticationFilter implements Filter {

    /**
     * The doFilter method establishes the account type of the user, and checks if the user is allowed to access
     * the requested page. If the user is not allowed to access the page, the user is served the custom error page.
     * @param request the <code>ServletRequest</code> object contains the client's request
     * @param response the <code>ServletResponse</code> object contains the filter's response
     * @param chain the <code>FilterChain</code> for invoking the next filter or the resource
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletReq = (HttpServletRequest) request;
        HttpServletResponse servletRes = (HttpServletResponse) response;

        Cookie[] cookies = servletReq.getCookies();

        String sessionId = null;
        String accountIdString = null;

        // by default, we assume the user is not logged in
        SessionInvalidReason sessionInvalidReason = SessionInvalidReason.NOT_LOGGED_IN;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    sessionId = cookie.getValue();
                }
                if (cookie.getName().equals("accountId")) {
                    accountIdString = cookie.getValue();
                }
            }
        }

        String path = servletReq.getRequestURI();
        AccountType requiredAccountType = PageMapping.getRequiredAccountType(path);

        if (requiredAccountType == null) {
            chain.doFilter(request, response);
            return;
        }

        AccountType accountType = null;

        try {
            accountType = SessionVerifier.determineAccountType(sessionId, accountIdString);
        } catch (InvalidSessionException e) {
            sessionInvalidReason = e.getReason();
        }

        if (requiredAccountType == accountType) {
            chain.doFilter(request, response);
            return;
        } else if (accountType != null) {
            sessionInvalidReason = SessionInvalidReason.UNAUTHORIZED;
        }

        String loginURL = servletReq.getContextPath() + "/login?path=" + servletReq.getRequestURI() + "&";

        switch (sessionInvalidReason) {
            case EXPIRED -> servletRes.sendRedirect(loginURL + "sessionExpired=true");
            case NOT_LOGGED_IN -> servletRes.sendRedirect(loginURL + "loginRequired=true");
            case UNAUTHORIZED -> servletRes.sendError(403, "You don't have the permission to view this page");
        }
    }
}
