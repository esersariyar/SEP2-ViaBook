package rmi;

import model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserServiceInterface extends Remote {
    
    User authenticateUser(String email, String password) throws RemoteException;
    
    boolean registerUser(String password, String email, String firstName, String lastName, String role) throws RemoteException;
    
    boolean isValidUser(String email) throws RemoteException;
    
    boolean updateUser(User user) throws RemoteException;
} 