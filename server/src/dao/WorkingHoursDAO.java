package dao;

import database.DatabaseConnector;
import model.WorkingHours;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WorkingHoursDAO {
    private DatabaseConnector dbConnector;
    
    public WorkingHoursDAO() {
        this.dbConnector = new DatabaseConnector();
    }
    
    public List<WorkingHours> getWorkingHoursByDentistId(int dentistId) {
        List<WorkingHours> workingHoursList = new ArrayList<>();
        String sql = "SELECT * FROM working_hours WHERE dentist_id = ? ORDER BY CASE day_of_week " +
                    "WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3 " +
                    "WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6 " +
                    "WHEN 'Sunday' THEN 7 END";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                WorkingHours workingHours = new WorkingHours();
                workingHours.setId(rs.getInt("id"));
                workingHours.setDentistId(rs.getInt("dentist_id"));
                workingHours.setDayOfWeek(rs.getString("day_of_week"));
                workingHours.setStartTime(rs.getTime("start_time").toLocalTime());
                workingHours.setEndTime(rs.getTime("end_time").toLocalTime());
                workingHoursList.add(workingHours);
            }
        } catch (SQLException e) {
            System.err.println("Error getting working hours: " + e.getMessage());
        }
        
        return workingHoursList;
    }
    
    public boolean addWorkingHours(WorkingHours workingHours) {
        String sql = "INSERT INTO working_hours (dentist_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, workingHours.getDentistId());
            stmt.setString(2, workingHours.getDayOfWeek());
            stmt.setTime(3, Time.valueOf(workingHours.getStartTime()));
            stmt.setTime(4, Time.valueOf(workingHours.getEndTime()));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding working hours: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateWorkingHours(WorkingHours workingHours) {
        String sql = "UPDATE working_hours SET start_time = ?, end_time = ? WHERE dentist_id = ? AND day_of_week = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTime(1, Time.valueOf(workingHours.getStartTime()));
            stmt.setTime(2, Time.valueOf(workingHours.getEndTime()));
            stmt.setInt(3, workingHours.getDentistId());
            stmt.setString(4, workingHours.getDayOfWeek());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating working hours: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteWorkingHours(int dentistId, String dayOfWeek) {
        String sql = "DELETE FROM working_hours WHERE dentist_id = ? AND day_of_week = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dentistId);
            stmt.setString(2, dayOfWeek);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting working hours: " + e.getMessage());
            return false;
        }
    }
} 