package misc;

import dao.MailService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.SQLException;

public class ApplicationConfig implements ServletContextListener {
    /**
     * A method that is being run on start up. Sets the connection up.
     * @param arg0 built-in arg
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ConnectionFactory.setup();
//        MailService.MAIL.setup();
    }

    /**
     * A method is being run on closing. Closes the connection safely.
     * @param arg0
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            ConnectionFactory.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
