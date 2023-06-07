package misc;

import dao.AccountDao;
import dao.SessionDao;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.AccountType;
import models.SessionBean;
import models.SessionValidity;

import java.io.IOException;
import java.sql.SQLException;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletReq = (HttpServletRequest) request;
        HttpServletResponse servletRes = (HttpServletResponse) response;

        Cookie[] cookies = servletReq.getCookies();

        String sessionId = null;
        String accountIdString = null;

        SessionValidity sessionValidity = SessionValidity.LOG_IN_REQUIRED;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    sessionId = cookie.getValue();
                }
                if (cookie.getName().equals("accountId")) {
                    accountIdString = cookie.getValue();
                }
            }

            String path = servletReq.getRequestURI();
            sessionValidity = getSessionValidity(sessionId, accountIdString, path);
            if (sessionValidity == SessionValidity.VALID) {
                chain.doFilter(request, response);
                return;
            }
        }
        String loginURL = servletReq.getContextPath() + "/pages/login/index.html?path=" + servletReq.getRequestURI() + "&";

        switch (sessionValidity) {
            case EXPIRED -> servletRes.sendRedirect(loginURL + "sessionExpired=true");
            case LOG_IN_REQUIRED -> servletRes.sendRedirect(loginURL + "loginRequired=true");
            case UNAUTHORIZED -> servletRes.sendError(403, "You don't have the permission to view this page");
        }
    }

    public SessionValidity getSessionValidity(String sessionId, String accountIdString, String path) {

        if (sessionId == null || accountIdString == null) {
            return SessionValidity.LOG_IN_REQUIRED;
        }

        int accountId;
        try {
            accountId = Integer.parseInt(accountIdString);
        } catch (NumberFormatException e) {
            // handle invalid account ID
            return SessionValidity.LOG_IN_REQUIRED;
        }

        SessionBean session = new SessionBean(accountId, sessionId);

        boolean sessionIsValid = SessionDao.instance.checkValidSession(session);

        if (sessionIsValid) {
            AccountType accountType;
            try {
                accountType = AccountDao.instance.determineAccountType(accountId);
            } catch (SQLException e) {
                return SessionValidity.UNAUTHORIZED;
            }
            if (path.contains("/admin/") && accountType == AccountType.ADMINISTRATOR) {
                return SessionValidity.VALID;
            } else if (path.contains("/crew/") && accountType == AccountType.CREW_MEMBER) {
                return SessionValidity.VALID;
            }
        } else {
            return SessionValidity.EXPIRED;
        }
        return SessionValidity.UNAUTHORIZED;
    }
}
