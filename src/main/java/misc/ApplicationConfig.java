package misc;

import dao.MailService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.SQLException;

public class ApplicationConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ConnectionFactory.setup();
//        MailService.MAIL.setup();
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            ConnectionFactory.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
