package dao;

import model.User;
import java.sql.SQLException;
import java.util.List;

public class UserDAO {
    
    public User getUserByUsername(String username) throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public User getUserById(int userId) throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public boolean createUser(User user) throws SQLException {
        // TODO: Implement method
        return false;
    }
    
    public boolean updateUser(User user) throws SQLException {
        // TODO: Implement method
        return false;
    }
    
    public boolean deleteUser(int userId) throws SQLException {
        // TODO: Implement method
        return false;
    }
    
    public List<User> getAllUsers() throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public boolean authenticateUser(String username, String password) throws SQLException {
        // TODO: Implement method
        return false;
    }
} 