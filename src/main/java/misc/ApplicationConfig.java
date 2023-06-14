package misc;

import dao.MailService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ApplicationConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ConnectionFactory.setup();
        MailService.MAIL.setup();
    }
}
