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
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        role VARCHAR(50) NOT NULL CHECK (role IN ('patient', 'dentist', 'secretary')),
                        first_name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("users table created (or already exists)");
            }
            
            try (Statement stmt = viabookConn.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dentist_profiles (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        description TEXT,
                        specialization VARCHAR(255)
                    )
                """);
                System.out.println("dentist_profiles table created (or already exists)");
            }
            
            try (Statement stmt = viabookConn.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS appointments (
                        id SERIAL PRIMARY KEY,
                        patient_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        dentist_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        appointment_time TIMESTAMP NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'cancelled')),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("appointments table created (or already exists)");
            }
            
            try (Statement stmt = viabookConn.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS working_hours (
                        id SERIAL PRIMARY KEY,
                        dentist_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        day_of_week VARCHAR(20) NOT NULL CHECK (day_of_week IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')),
                        start_time TIME NOT NULL,
                        end_time TIME NOT NULL,
                        CONSTRAINT working_hours_time_check CHECK (start_time < end_time),
                        UNIQUE(dentist_id, day_of_week)
                    )
                """);
                System.out.println("working_hours table created (or already exists)");
            }
            
            try (Statement stmt = viabookConn.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS blocked_slots (
                        id SERIAL PRIMARY KEY,
                        dentist_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        blocked_time TIMESTAMP NOT NULL,
                        reason VARCHAR(255),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("blocked_slots table created (or already exists)");
            }
            
            try (Statement stmt = viabookConn.createStatement()) {
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_role ON users(role)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_dentist_profiles_user_id ON dentist_profiles(user_id)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_appointments_dentist_id ON appointments(dentist_id)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_appointments_time ON appointments(appointment_time)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_working_hours_dentist_id ON working_hours(dentist_id)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_working_hours_day ON working_hours(day_of_week)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_blocked_slots_dentist_id ON blocked_slots(dentist_id)");
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_blocked_slots_time ON blocked_slots(blocked_time)");
                System.out.println("Database indexes created (or already exist)");
            }
            
            try (Statement stmt = viabookConn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email = 'patient@patient.com'");
                rs.next();
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