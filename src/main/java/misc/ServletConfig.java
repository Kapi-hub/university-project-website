package misc;

import com.fasterxml.jackson.core.util.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ServletConfig extends ResourceConfig {

    public ServletConfig() {
        packages("dao", "misc", "models", "resources");
        register(AuthenticationFilter.class);
        register(JacksonFeature.class);
    }
}
