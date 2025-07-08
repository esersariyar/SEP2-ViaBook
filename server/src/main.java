import database.DatabaseConnector;
import rmi.UserServiceImpl;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Main {
    
    public static void main(String[] args) {
        try {
            System.out.println("ViaBook Server starting...");
            
            DatabaseConnector dbConnector = new DatabaseConnector();
            dbConnector.initializeDatabase();
            
            // Start RMI registry
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry started on port 1099");
            
            // Create and bind UserService
            UserServiceImpl userService = new UserServiceImpl();
            Naming.rebind("rmi://localhost:1099/UserService", userService);
            
            System.out.println("ViaBook Server is running!");
            System.out.println("UserService bound to RMI registry");
            System.out.println("Waiting for client connections...");
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("ViaBook Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 