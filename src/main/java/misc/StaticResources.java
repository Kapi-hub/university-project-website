package misc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class StaticResources extends HttpServlet {

    private static final String PUBLIC_URL = "/static/";
    private static final String PRIVATE_PATH = "/WEB-INF/publicResources/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(
                request.getRequestURI()
                        .replace(PUBLIC_URL, PRIVATE_PATH)
        ).forward(request, response);
    }
}
