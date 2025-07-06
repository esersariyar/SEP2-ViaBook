package rmi;

import service.UserService;
import model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
    public boolean registerUser(String password, String email, String role) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Registration attempt for email: " + email);
            return userService.registerUser(password, email, role);
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
} 