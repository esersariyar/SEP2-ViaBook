package dao;

import database.DatabaseConnector;
import model.DentistProfile;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistProfileDAO {
    private DatabaseConnector dbConnector;
    
    public DentistProfileDAO() {
        this.dbConnector = new DatabaseConnector();
    }
    
    public DentistProfile getProfileByUserId(int userId) {
        String sql = "SELECT * FROM dentist_profiles WHERE user_id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                DentistProfile profile = new DentistProfile();
                profile.setId(rs.getInt("id"));
                profile.setUserId(rs.getInt("user_id"));
                profile.setDescription(rs.getString("description"));
                profile.setSpecialization(rs.getString("specialization"));
                return profile;
            }
        } catch (SQLException e) {
            System.err.println("Error getting dentist profile: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean createOrUpdateProfile(DentistProfile profile) {
        DentistProfile existing = getProfileByUserId(profile.getUserId());
        
        if (existing != null) {
            return updateProfile(profile);
        } else {
            return createProfile(profile);
        }
    }
    
    private boolean createProfile(DentistProfile profile) {
        String sql = "INSERT INTO dentist_profiles (user_id, description, specialization) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getDescription());
            stmt.setString(3, profile.getSpecialization());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating dentist profile: " + e.getMessage());
            return false;
        }
    }
    
    private boolean updateProfile(DentistProfile profile) {
        String sql = "UPDATE dentist_profiles SET description = ?, specialization = ? WHERE user_id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profile.getDescription());
            stmt.setString(2, profile.getSpecialization());
            stmt.setInt(3, profile.getUserId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating dentist profile: " + e.getMessage());
            return false;
        }
    }
} 