package misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerConfig {
    static Connection connection;
    static String URL = "jdbc:postgresql://bronto.ewi.utwente.nl/";
    static String DB_USER = "dab_di22232b_249";
    static String SCHEMA = "?currentSchema=shotmaniacs/";
    static String FILE_NAME = "di-2023-project-password";

    static void setup() {
        try {
            Class.forName("org.postgresql.Driver");
            String password = readPassword();
            System.out.println(password);
            connection = DriverManager.getConnection(URL+DB_USER+SCHEMA, DB_USER, password);
            System.out.println("Connection successfully setup.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Driver is not loaded.");
        } catch (SQLException e) {
            System.err.println("Oops: "+e.getMessage());
        }
    }

    private static String readPassword() {
        try {
            String path = System.getProperty("user.dir") + "/";
            var br = new BufferedReader(new FileReader(path+FILE_NAME));
            return br.readLine().strip();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error: 'password' file does not exist.");
            return "";
        } catch (IOException e) {
            System.err.println("Error reading document: " + e.getMessage());
            return "";
        }
    }
}
