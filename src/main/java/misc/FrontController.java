package misc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Class to handle the front controller
 */
public class FrontController extends HttpServlet {

    /**
     * Method to handle the GET request
     * It is used to redirect the user to the correct page
     * This is done by looking up the url in the PageMapping class
     * If the url is not found, the user is redirected to the error page
     * If the url is found, the user is served the correct file located in the WEB-INF/pages/ folder
     * @param request the request
     * @param response the response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getRequestURI();

        if (url.startsWith("/WEB-INF/")) {
            request.getServletContext().getNamedDispatcher("default").forward(request, response);
            return;
        }

        String resourceLocation = PageMapping.getResourceLocation(url);

        if (resourceLocation == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resourceLocation = "/WEB-INF/pages" + resourceLocation;

        request.getRequestDispatcher(resourceLocation).forward(request, response);
    }
}