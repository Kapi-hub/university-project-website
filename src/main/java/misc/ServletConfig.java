package misc;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 * A class that sets up the api
 */
public class ServletConfig extends ResourceConfig {

    /**
     * Sets up the api by registering the packages and the authentication filter
     * as well as enabling the roles allowed dynamic feature, which allows us to use the @RolesAllowed annotation
     */
    public ServletConfig() {
        packages("dao", "misc", "models", "resources");
        register(RolesAllowedDynamicFeature.class);
        register(JerseyAuthenticationFilter.class);
    }
}
