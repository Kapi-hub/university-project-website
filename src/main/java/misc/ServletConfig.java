package misc;

import org.glassfish.jersey.server.ResourceConfig;

public class ServletConfig extends ResourceConfig {

    public ServletConfig() {

        register(AuthenticationFilter.class);

    }


}
