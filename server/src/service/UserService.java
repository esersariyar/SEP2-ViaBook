import java.util.List;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public User authenticateUser(String username, String password) {
        if (username == null || password == null || 
            username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        
        return userDAO.validateLogin(username, password);
    }
    
    public boolean registerUser(String username, String password, String email, String role) {
        if (username == null || password == null || email == null || role == null ||
            username.trim().isEmpty() || password.trim().isEmpty() || 
            email.trim().isEmpty() || role.trim().isEmpty()) {
            return false;
        }
        
        User newUser = new User(username, password, email, role);
        return userDAO.createUser(newUser);
    }
    
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    public boolean isValidUser(String username) {
        return username != null && !username.trim().isEmpty();
    }
} 