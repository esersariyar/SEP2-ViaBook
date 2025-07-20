import database.DatabaseConnector;
import rmi.UserServiceImpl;
import dao.UserDAO;
import dao.WorkingHoursDAO;
import model.User;
import model.WorkingHours;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

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
            UserDAO userDAO = new UserDAO();
            WorkingHoursDAO workingHoursDAO = new WorkingHoursDAO();
            List<User> allUsers = userDAO.getAllUsers();
            String[][] demoUsers = {
                {"bob@example.com", "Bob", "Smith", "patient"},
                {"wendy@example.com", "Wendy", "Brown", "patient"},
                {"eser@example.com", "Eser", "Sariyar", "secretary"},
                {"john@example.com", "John", "Doe", "dentist"}
            };
            String defaultPassword = "password";
            Random random = new Random();
            
            for (String[] demo : demoUsers) {
                boolean exists = allUsers.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(demo[0]));
                if (!exists) {
                    User user = new User(defaultPassword, demo[0], demo[1], demo[2], demo[3]);
                    userDAO.createUser(user);
                    System.out.println("Demo user created: " + demo[1] + " (" + demo[3] + ")");
                    
                    // If it's a dentist, add random working hours
                    if ("dentist".equals(demo[3])) {
                        addRandomWorkingHours(user.getId(), workingHoursDAO, random);
                    }
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
    
    private static void addRandomWorkingHours(int dentistId, WorkingHoursDAO workingHoursDAO, Random random) {
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        
        // Add 3-5 random weekdays
        int daysToAdd = 3 + random.nextInt(3); // 3-5 days
        boolean[] selectedDays = new boolean[5];
        
        for (int i = 0; i < daysToAdd; i++) {
            int dayIndex;
            do {
                dayIndex = random.nextInt(5);
            } while (selectedDays[dayIndex]);
            
            selectedDays[dayIndex] = true;
            String dayOfWeek = weekdays[dayIndex];
            
            // Random start time between 9:00 and 14:00
            int startHour = 9 + random.nextInt(6); // 9-14
            int startMinute = random.nextInt(4) * 15; // 0, 15, 30, 45
            LocalTime startTime = LocalTime.of(startHour, startMinute);
            
            // Random end time between start time + 4 hours and 17:00
            int endHour = Math.min(17, startHour + 4 + random.nextInt(3)); // start + 4-6 hours, max 17
            int endMinute = random.nextInt(4) * 15; // 0, 15, 30, 45
            LocalTime endTime = LocalTime.of(endHour, endMinute);
            
            // Ensure end time is after start time
            if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                endTime = startTime.plusHours(4);
            }
            
            WorkingHours workingHours = new WorkingHours(dentistId, dayOfWeek, startTime, endTime);
            if (workingHoursDAO.addWorkingHours(workingHours)) {
                System.out.println("Added working hours for " + dayOfWeek + ": " + startTime + " - " + endTime);
            }
        }
    }
} 