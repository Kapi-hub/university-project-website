package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory Pattern
 */
public class ConnectionFactory {
    private static final boolean USE_PREVIDER_DB = false;

    private static final String BRONTO_HOST = "bronto.ewi.utwente.nl";
    private static final String BRONTO_DB_NAME = "dab_di22232b_249";
    private static final String BRONTO_USERNAME = "dab_di22232b_249";

    private static final String PREVIDER_HOST = "node29422-shotmaniacs1.paas.hosted-by-previder.com";
    private static final String PREVIDER_DB_NAME = "postgres";
    private static final String PREVIDER_USERNAME = "webadmin";

    private static Connection connection;
    private static boolean connected = false;

    public static Connection getConnection() {
        if (!connected) setup();
        return connection;
    }

    public static void setup() {
        String URL = "jdbc:postgresql://%s:5432/%s?currentSchema=shotmaniacs%s";

        String host = USE_PREVIDER_DB ? PREVIDER_HOST : BRONTO_HOST;
        String dbName = USE_PREVIDER_DB ? PREVIDER_DB_NAME : BRONTO_DB_NAME;
        String username = USE_PREVIDER_DB ? PREVIDER_USERNAME : BRONTO_USERNAME;

        URL = String.format(URL, host, dbName, USE_PREVIDER_DB ? "" : "1");

        try {
            Class.forName("org.postgresql.Driver");
            String password = readPassword();
            if (password == null) throw new NullPointerException("Password env variable could not be found.");
            connection = DriverManager.getConnection(URL, username, password);
            connected = true;
            System.out.println("Connection to database successfully setup.");
        } catch (ClassNotFoundException e) {
            System.err.printf("Driver is not loaded: %s\n", e.getMessage());
        } catch (SQLException e) {
            System.err.printf("Oops: %s\n", e.getMessage());
        } catch (NullPointerException e) {
            System.err.printf("Error: %s\n", e.getMessage());
        }
    }

    private static String readPassword() {
        return System.getenv("DB_PASSWORD");
    }
}