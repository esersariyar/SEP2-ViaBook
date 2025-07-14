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

            // --- DEMO USERS ---
            dao.UserDAO userDAO = new dao.UserDAO();
            java.util.List<model.User> allUsers = userDAO.getAllUsers();
            String[][] demoUsers = {
                {"bob@example.com", "Bob", "Smith", "patient"},
                {"wendy@example.com", "Wendy", "Brown", "patient"},
                {"eser@example.com", "Eser", "Sariyar", "secretary"},
                {"john@example.com", "John", "Doe", "dentist"}
            };
            String defaultPassword = "password";
            for (String[] demo : demoUsers) {
                boolean exists = allUsers.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(demo[0]));
                if (!exists) {
                    model.User user = new model.User(defaultPassword, demo[0], demo[1], demo[2], demo[3]);
                    userDAO.createUser(user);
                    System.out.println("Demo user created: " + demo[1] + " (" + demo[3] + ")");
                }
            }
            // --- DEMO USERS ---

            // --- THREAD DEMO ---
            java.util.concurrent.BlockingQueue<model.Appointment> queue = new java.util.concurrent.LinkedBlockingQueue<>();
            int dentistCount = 5;
            int patientCount = 10;
            service.AppointmentProducer producer = new service.AppointmentProducer(queue, dentistCount, patientCount);
            service.SecretaryConsumer consumer = new service.SecretaryConsumer(queue);
            producer.start();
            consumer.start();
            // --- THREAD DEMO ---

            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("ViaBook Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 