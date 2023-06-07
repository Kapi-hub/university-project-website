package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory Pattern
 */
public class ConnectionFactory {
    private static final String HOST = "node29422-shotmaniacs1.paas.hosted-by-previder.com";
    private static final String DB_NAME = "postgres";
    private static final String URL = "jdbc:postgresql://" + HOST + ":5432/" + DB_NAME + "?currentSchema=shotmaniacs";
    private static final String USERNAME = "webadmin";


//    private static final String FILE_NAME = "di-2023-project-password";
    private static Connection connection;
    private static boolean connected = false;

    public static void setup() {
        try {
            Class.forName("org.postgresql.Driver");
            String password = readPassword();

            System.out.println(password);
            connection = DriverManager.getConnection(URL, USERNAME, password);
            connected = true;
            System.out.println("Connection successfully setup.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Driver is not loaded.");
        } catch (SQLException e) {
            System.err.println("Oops: "+e.getMessage());
        }
    }

    public static Connection getConnection() {
        if (!connected) setup();
        return connection;
    }

    private static String readPassword() {
        return "EFPsql77037";
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
}