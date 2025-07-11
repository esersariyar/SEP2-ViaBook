package rmi;

import service.UserService;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class UserServiceImpl extends UnicastRemoteObject implements UserServiceInterface {
    private UserService userService;
    
    public UserServiceImpl() throws RemoteException {
        super();
        this.userService = new UserService();
    }
    
    @Override
    public User authenticateUser(String email, String password) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Login attempt for email: " + email);
            
            User user = userService.authenticateUser(email, password);
            if (user != null) {
                System.out.println("ViaBook Server: Login successful for " + email);
                return user;
            }
            
            System.out.println("ViaBook Server: Login failed for " + email);
            return null;
            
        } catch (Exception e) {
            System.err.println("ViaBook Server: Authentication error: " + e.getMessage());
            throw new RemoteException("Authentication failed", e);
        }
    }
    
    @Override
    public boolean registerUser(String password, String email, String firstName, String lastName, String role) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Registration attempt for email: " + email);
            return userService.registerUser(password, email, firstName, lastName, role);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Registration error: " + e.getMessage());
            throw new RemoteException("Registration failed", e);
        }
    }
    
    @Override
    public boolean isValidUser(String email) throws RemoteException {
        try {
            return userService.isValidUser(email);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Validation error: " + e.getMessage());
            throw new RemoteException("User validation failed", e);
        }
    }
    
    @Override
    public boolean updateUser(User user) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update attempt for user: " + user.getEmail());
            return userService.updateUser(user);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update error: " + e.getMessage());
            throw new RemoteException("User update failed", e);
        }
    }
    
    @Override
    public List<User> getAllUsers() throws RemoteException {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get all users error: " + e.getMessage());
            throw new RemoteException("Get all users failed", e);
        }
    }
    
    @Override
    public boolean deleteUser(int userId) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Delete attempt for user ID: " + userId);
            return userService.deleteUser(userId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Delete error: " + e.getMessage());
            throw new RemoteException("User delete failed", e);
        }
    }
    
    @Override
    public DentistProfile getDentistProfile(int userId) throws RemoteException {
        try {
            return userService.getDentistProfile(userId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get dentist profile error: " + e.getMessage());
            throw new RemoteException("Get dentist profile failed", e);
        }
    }
    
    @Override
    public boolean updateDentistProfile(DentistProfile profile) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update dentist profile attempt for user ID: " + profile.getUserId());
            return userService.updateDentistProfile(profile);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update dentist profile error: " + e.getMessage());
            throw new RemoteException("Update dentist profile failed", e);
        }
    }
    
    @Override
    public List<WorkingHours> getWorkingHours(int dentistId) throws RemoteException {
        try {
            return userService.getWorkingHours(dentistId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get working hours error: " + e.getMessage());
            throw new RemoteException("Get working hours failed", e);
        }
    }
    
    @Override
    public boolean addWorkingHours(WorkingHours workingHours) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Add working hours attempt for dentist ID: " + workingHours.getDentistId());
            return userService.addWorkingHours(workingHours);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Add working hours error: " + e.getMessage());
            throw new RemoteException("Add working hours failed", e);
        }
    }
    
    @Override
    public boolean updateWorkingHours(WorkingHours workingHours) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update working hours attempt for dentist ID: " + workingHours.getDentistId());
            return userService.updateWorkingHours(workingHours);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update working hours error: " + e.getMessage());
            throw new RemoteException("Update working hours failed", e);
        }
    }
    
    @Override
    public boolean deleteWorkingHours(int dentistId, String dayOfWeek) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Delete working hours attempt for dentist ID: " + dentistId + ", day: " + dayOfWeek);
            return userService.deleteWorkingHours(dentistId, dayOfWeek);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Delete working hours error: " + e.getMessage());
            throw new RemoteException("Delete working hours failed", e);
        }
    }
} 