package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory Pattern
 */
public class ConnectionFactory {
    private static final int DB = 1; // 1 = Bronto, 2 = Previder
    private static final String PREVIDER_HOST = "node29422-shotmaniacs1.paas.hosted-by-previder.com";
    private static final String BRONTO_HOST = "bronto.ewi.utwente.nl";
    private static final String PREVIDER_DB_NAME = "postgres";
    private static final String BRONTO_DB_NAME = "dab_di22232b_249";
    private static final String PREVIDER_URL = "jdbc:postgresql://" + PREVIDER_HOST + ":5432/" + PREVIDER_DB_NAME + "?currentSchema=shotmaniacs";
    private static final String BRONTO_URL = "jdbc:postgresql://" + BRONTO_HOST + ":5432/" + BRONTO_DB_NAME + "?currentSchema=shotmaniacs1";
    private static final String PREVIDER_USERNAME = "webadmin";
    private static final String BRONTO_USERNAME = "dab_di22232b_249";
    private static Connection connection;
    private static boolean connected = false;
    //    private static final String FILE_NAME = "di-2023-project-password";

    public static Connection getConnection() {
        if (!connected) setup();
        return connection;
    }

    public static void setup() {
        if (DB == 1) brontoFactory();
        else if (DB == 2) previderFactory();
        else System.err.println("Error: no valid database selected.");
    }

    private static String readPassword(int db) {
        if (db == 1) return "Y8AHMmfXpf9glCor";
        if (db == 2) return "EFPsql77037";
        else return null;
//        try {
//            String path = System.getProperty("user.dir")+"/";
//            var br = new BufferedReader(new FileReader(path+FILE_NAME));
//            return br.readLine().strip();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.err.println("Error: 'password' file does not exist.");
//            return "";
//        } catch (IOException e) {
//            System.err.println("Error reading document: "+e.getMessage());
//            return "";
//        }
    }

    public static void brontoFactory() {
        try {
            Class.forName("org.postgresql.Driver");
            String password = readPassword(1);
            System.out.println(password);
            connection = DriverManager.getConnection(BRONTO_URL, BRONTO_USERNAME, password);
            connected = true;
            System.out.println("Connection successfully setup.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Driver is not loaded.");
        } catch (SQLException e) {
            System.err.println("Oops: " + e.getMessage());
        }
    }

    public static void previderFactory() {
        try {
            Class.forName("org.postgresql.Driver");
            String password = readPassword(2);
            System.out.println(password);
            connection = DriverManager.getConnection(PREVIDER_URL, PREVIDER_USERNAME, password);
            connected = true;
            System.out.println("Connection successfully setup.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Driver is not loaded.");
        } catch (SQLException e) {
            System.err.println("Oops: " + e.getMessage());
        }
    }
}
