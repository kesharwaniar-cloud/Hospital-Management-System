import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root",
                "kesharwani123@"
            );
            System.out.println("Database Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}