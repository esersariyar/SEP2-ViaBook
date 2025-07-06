import java.rmi.Naming;
import java.rmi.RemoteException;
import rmi.UserServiceInterface;
import model.User;

public class RMIClient {
    private static final String SERVER_BASE_URL = "rmi://localhost:1099/";
    private UserServiceInterface userService;
    
    public RMIClient() {
        connectToServer();
    }
    
    private void connectToServer() {
        try {
            System.out.println("ViaBook Client: Connecting to server...");
            userService = (UserServiceInterface) Naming.lookup(SERVER_BASE_URL + "UserService");
            System.out.println("ViaBook Client: Connected to server successfully");
        } catch (Exception e) {
            System.err.println("ViaBook Client: Failed to connect to server: " + e.getMessage());
            userService = null;
        }
    }
    
    public User authenticateUser(String email, String password) {
        if (userService == null) {
            System.err.println("ViaBook Client: No server connection available");
            return null;
        }
        
        try {
            return userService.authenticateUser(email, password);
        } catch (RemoteException e) {
            System.err.println("ViaBook Client: Authentication failed: " + e.getMessage());
            return null;
        }
    }
    
    public boolean registerUser(String password, String email, String firstName, String lastName, String role) {
        if (userService == null) {
            System.err.println("ViaBook Client: No server connection available");
            return false;
        }
        
        try {
            return userService.registerUser(password, email, firstName, lastName, role);
        } catch (RemoteException e) {
            System.err.println("ViaBook Client: Registration failed: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isServerConnected() {
        return userService != null;
    }
    
    public void reconnect() {
        connectToServer();
    }
    
    public UserServiceInterface getUserService() {
        return userService;
    }
} 