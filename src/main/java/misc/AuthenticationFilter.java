package misc;

import dao.AccountDao;
import models.SessionValidity;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.AccountBean;
import models.AccountType;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletReq = (HttpServletRequest) request;
        HttpServletResponse servletRes = (HttpServletResponse) response;

        Cookie[] cookies = servletReq.getCookies();

        String sessionId = null;
        String accountIdString = null;

        SessionValidity  sessionValidity;

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
            sessionValidity = isValidSession(sessionId, accountIdString, path);
            if (sessionValidity == SessionValidity.VALID) {
                chain.doFilter(request, response);
            }
        } else {
            sessionValidity = SessionValidity.LOG_IN_REQUIRED;
        }
        String loginURL = servletReq.getContextPath() + "/pages/login/index.html?path=" + servletReq.getRequestURI() + "&";
        switch (sessionValidity) {
            case EXPIRED -> servletRes.sendRedirect(loginURL + "sessionExpired=true");
            case LOG_IN_REQUIRED -> servletRes.sendRedirect(loginURL + "loginRequired=true");
            case UNAUTHORIZED -> servletRes.sendError(403, "You don't have the permission to view this page");
        }
    }

    public SessionValidity isValidSession(String sessionId, String accountIdString, String path) {

        if (sessionId == null || accountIdString == null) {
            return SessionValidity.LOG_IN_REQUIRED;
        }

        int accountId = Integer.parseInt(accountIdString);
        AccountBean account = new AccountBean(accountId, sessionId);

        if (AccountDao.instance.checkSession(account)) {
            if (path.contains("/admin/") && account.getAccountType() == AccountType.ADMINISTRATOR) {
                return SessionValidity.VALID;
            } else if (path.contains("/crew/") && account.getAccountType() == AccountType.CREW_MEMBER) {
                return SessionValidity.VALID;
            }
        } else {
            return SessionValidity.EXPIRED;
        }
        return SessionValidity.UNAUTHORIZED;
    }
}
