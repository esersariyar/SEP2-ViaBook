import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://localhost:5432/viabook";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";
    
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL driver not found", e);
        }
    }
    
    public void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("ViaBook Database connection successful!");
        } catch (SQLException e) {
            System.err.println("ViaBook Database connection failed: " + e.getMessage());
        }
    }
} 