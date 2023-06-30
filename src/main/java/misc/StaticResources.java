package misc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Class to handle the static resources
 * It is used to serve the static resources
 * This class comes into play when the user requests a resource from the /static/ folder
 * This folder is where publicly accessible resources are stored
 * For instance, the css files, the images, the javascript files, etc.
 * If the url is found, the user is served the correct file located in the WEB-INF/publicResources/ folder
 */
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
