package misc;

import java.io.PrintWriter;

public class Custom403Response {
    public static void writeCustom403Page(PrintWriter writer) {
        writer.println("<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: #f1f1f1; display: flex; justify-content: center; align-items: center; height: 100vh; }" +
                ".content { text-align: center; padding: 20px; background: #fff; border-radius: 5px; }" +
                "h1 { color: #333; }" +
                "p { color: #666; }" +
                ".button { display: inline-block; margin-top: 20px; color: #fff; background-color: #007BFF; padding: 10px 20px; text-decoration: none; border-radius: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='content'>" +
                "<h1>Access Forbidden</h1>" +
                "<p>Sorry, but you are not authorized to access this page.</p>" +
                "<a href='/login' class='button'>Back to Login Form</a>" +
                "</div>" +
                "</body>" +
                "</html>"
        );
        writer.flush();
    }
}
