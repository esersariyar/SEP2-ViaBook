package rmi;

import model.User;
import model.DentistProfile;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserServiceInterface extends Remote {
    
    User authenticateUser(String email, String password) throws RemoteException;
    
    boolean registerUser(String password, String email, String firstName, String lastName, String role) throws RemoteException;
    
    boolean isValidUser(String email) throws RemoteException;
    
    boolean updateUser(User user) throws RemoteException;
    
    List<User> getAllUsers() throws RemoteException;
    
    boolean deleteUser(int userId) throws RemoteException;
    
    DentistProfile getDentistProfile(int userId) throws RemoteException;
} 