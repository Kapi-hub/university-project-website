package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory Pattern
 */
public class ConnectionFactory {
    private static boolean USE_PREVIDER_DB = false;

    private static final String BRONTO_HOST = "bronto.ewi.utwente.nl";
    private static final String BRONTO_DB_NAME = "dab_di22232b_249";
    private static final String BRONTO_USERNAME = "dab_di22232b_249";

    private static final String PREVIDER_HOST = "node29422-shotmaniacs1.paas.hosted-by-previder.com";
    private static final String PREVIDER_DB_NAME = "postgres";
    private static final String PREVIDER_USERNAME = "webadmin";

    private static final String DEFAULT_URL = "jdbc:postgresql://%s:5432/%s?currentSchema=shotmaniacs1";

    private static String password;

    private static Connection connection;
    private static boolean connected = false;

    /**
     * Returns the connection
     * If not connected, it sets up the connection.
     * @return the connection
     */
    public static Connection getConnection() {
        if (!connected) setup();
        return connection;
    }

    /**
     * Sets up the database connection.
     * Depending on the USE_PREVIDER_DB variable, it chooses a specific database.
     */
    public static void setup() {
        String host = USE_PREVIDER_DB ? PREVIDER_HOST : BRONTO_HOST;
        String dbName = USE_PREVIDER_DB ? PREVIDER_DB_NAME : BRONTO_DB_NAME;
        String username = USE_PREVIDER_DB ? PREVIDER_USERNAME : BRONTO_USERNAME;

        String URL = String.format(DEFAULT_URL, host, dbName);

        try {
            Class.forName("org.postgresql.Driver");
            if (password == null) {
                password = readPassword();
            }
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

    /**
     * A method to read the password safely.
     * @return the read password
     */
    private static String readPassword() {
        return System.getenv("DB_PASSWORD");
    }

    /**
     * Reconnects when something has been changed remotely.
     */
    public static void reconnect() {
        try {
            connection.close();
            connected = false;
            setup();
        } catch (SQLException e) {
            System.err.printf("Oops: %s\n", e.getMessage());
        }
    }

    /**
     * Setter for the previder bool
     * @param bool
     */
    public static void setPreviderBool (boolean bool) {
        USE_PREVIDER_DB = bool;
    }

    /**
     * Alternative way of password reading.
     * @param pass the pass it will read.
     */
    public static void setDbPassword (String pass) {
        password = pass;
    }
}