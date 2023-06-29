package misc;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class ServletConfig extends ResourceConfig {

    public ServletConfig() {
        packages("dao", "misc", "models", "resources");
        register(RolesAllowedDynamicFeature.class);
        register(JerseyAuthenticationFilter.class);
    }
}
