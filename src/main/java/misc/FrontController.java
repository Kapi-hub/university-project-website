package misc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FrontController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Arrived in FrontController");
        String url = request.getRequestURI();

        if (url.startsWith("/WEB-INF/")) {
            request.getServletContext().getNamedDispatcher("default").forward(request, response);
            return;
        }

        String resourceLocation = PageMapping.getResourceLocation(url);

        System.out.println("url is: " + url);
        System.out.println("Location is: " + resourceLocation);
        if (resourceLocation == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resourceLocation = "/WEB-INF/pages" + resourceLocation;

        request.getRequestDispatcher(resourceLocation).forward(request, response);
    }
}