package misc;

import org.glassfish.jersey.server.ResourceConfig;

public class ServletConfig extends ResourceConfig {

    public ServletConfig() {
        packages("dao", "misc", "models", "resources");
        System.out.println("ServletConfig constructor called");
        register(JerseyAuthenticationFilter.class);
    }
}
