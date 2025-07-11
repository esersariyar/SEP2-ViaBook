package service;

import dao.UserDAO;
import dao.DentistProfileDAO;
import model.User;
import model.DentistProfile;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private DentistProfileDAO dentistProfileDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
        this.dentistProfileDAO = new DentistProfileDAO();
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
    
    public boolean updateUser(User user) {
        if (user == null || user.getEmail() == null || user.getFirstName() == null || user.getLastName() == null ||
            user.getEmail().trim().isEmpty() || user.getFirstName().trim().isEmpty() || user.getLastName().trim().isEmpty()) {
            return false;
        }
        
        return userDAO.updateUser(user);
    }
    
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
    
    public DentistProfile getDentistProfile(int userId) {
        return dentistProfileDAO.getProfileByUserId(userId);
    }
    
    public boolean updateDentistProfile(DentistProfile profile) {
        if (profile == null || profile.getUserId() <= 0) {
            return false;
        }
        
        return dentistProfileDAO.createOrUpdateProfile(profile);
    }
} 