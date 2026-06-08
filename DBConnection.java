import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; // Verified blank setting for XAMPP
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.out.println("JDBC Driver not found!");
                e.printStackTrace();
            }
        }
        return connection;
    }
}