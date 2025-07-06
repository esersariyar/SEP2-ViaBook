package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnector {
    private static final String BASE_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DATABASE_NAME = "viabook";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "eser123";
    private static boolean initialized = false;
    
    public void initializeDatabase() {
        if (initialized) return;
        try {
            Class.forName("org.postgresql.Driver");
            Connection viabookConn = null;
            try {
                viabookConn = DriverManager.getConnection(BASE_URL + DATABASE_NAME, USERNAME, PASSWORD);
                System.out.println("Connected to viabook database");
            } catch (SQLException e) {
                System.out.println("viabook database does not exist, creating now...");
                try (Connection postgresConn = DriverManager.getConnection(BASE_URL + "postgres", USERNAME, PASSWORD);
                     Statement stmt = postgresConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE " + DATABASE_NAME);
                    System.out.println("viabook database created!");
                }
                viabookConn = DriverManager.getConnection(BASE_URL + DATABASE_NAME, USERNAME, PASSWORD);
            }
            try (Statement stmt = viabookConn.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        first_name VARCHAR(100),
                        last_name VARCHAR(100),
                        role VARCHAR(20) NOT NULL CHECK (role IN ('patient', 'dentist', 'secretary')),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("users table created (or already exists)");
            }
            // Insert default test user if not exists
            try (Statement stmt = viabookConn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'test@test.com'");
                rs.next();
                if (rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO users (email, password, first_name, last_name, role) VALUES ('test@test.com', 'test', 'Test', 'User', 'patient')");
                    System.out.println("Default test user inserted");
                }
            }
            viabookConn.close();
            initialized = true;
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (!initialized) {
            initializeDatabase();
        }
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(BASE_URL + DATABASE_NAME, USERNAME, PASSWORD);
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