package misc;

import dao.MailService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ApplicationConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ConnectionFactory.setup();
        MailService.MAIL.setup();
    }
}
