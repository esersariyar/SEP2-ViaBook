package service;

import dao.UserDAO;
import model.User;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public User authenticateUser(String email, String password) {
        if (email == null || password == null ||
            email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        
        return userDAO.validateLogin(email, password);
    }
    
    public boolean registerUser(String password, String email, String firstName, String lastName, String role) {
        if (password == null || email == null || firstName == null || lastName == null || role == null ||
            password.trim().isEmpty() || email.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty() || role.trim().isEmpty()) {
            return false;
        }
        
        User newUser = new User(password, email, firstName, lastName, role);
        return userDAO.createUser(newUser);
    }
    
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    public boolean isValidUser(String email) {
        return email != null && !email.trim().isEmpty();
    }
} 