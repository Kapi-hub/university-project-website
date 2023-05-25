package misc;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ServletConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServerConfig.setup();
    }
}
