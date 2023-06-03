package misc;

import dao.AccountDao;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.AccountBean;
import models.AccountType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletReq = (HttpServletRequest) request;
        HttpServletResponse servletRes = (HttpServletResponse) response;

        Cookie[] cookies = servletReq.getCookies();

        String sessionId = null;
        String accountIdString = null;

        boolean sessionIsValid = false;

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
            if (isValidSession(sessionId, accountIdString, path)) {
                chain.doFilter(request, response);
                sessionIsValid = true;
            }

        }
        if (!sessionIsValid) {
            String loginURL = servletReq.getContextPath() + "/pages/login/index.html?loginRequiredForPath=" + servletReq.getRequestURI();
            servletRes.sendRedirect(loginURL);
//            servletRes.sendError(401, "You're not allowed to see this page. Please log in.");

        }
    }

    public boolean isValidSession(String sessionId, String accountIdString, String path) {

        if (sessionId == null || accountIdString == null) {
            return false;
        }

        int accountId = Integer.parseInt(accountIdString);
        AccountBean account = new AccountBean(accountId, sessionId);

        if (AccountDao.instance.checkSession(account)) {
            if (path.contains("/admin/") && account.getAccountType() == AccountType.ADMINISTRATOR) {
                return true;
            } else return path.contains("/crew/") && account.getAccountType() == AccountType.CREW_MEMBER;
        }
        System.out.println("Session is invalid");
        return false;
    }
}
