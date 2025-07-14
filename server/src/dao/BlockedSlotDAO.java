package dao;

import database.DatabaseConnector;
import model.BlockedSlot;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlockedSlotDAO {
    private DatabaseConnector dbConnector;
    
    public BlockedSlotDAO() {
        this.dbConnector = new DatabaseConnector();
    }
    
    public boolean createBlockedSlot(BlockedSlot blockedSlot) {
        String sql = "INSERT INTO blocked_slots (dentist_id, blocked_time, reason) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, blockedSlot.getDentistId());
            stmt.setTimestamp(2, Timestamp.valueOf(blockedSlot.getBlockedTime()));
            stmt.setString(3, blockedSlot.getReason());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating blocked slot: " + e.getMessage());
            return false;
        }
    }
    
    public List<BlockedSlot> getBlockedSlotsByDentistId(int dentistId) {
        List<BlockedSlot> blockedSlots = new ArrayList<>();
        String sql = "SELECT * FROM blocked_slots WHERE dentist_id = ? ORDER BY blocked_time";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BlockedSlot blockedSlot = new BlockedSlot();
                blockedSlot.setId(rs.getInt("id"));
                blockedSlot.setDentistId(rs.getInt("dentist_id"));
                blockedSlot.setBlockedTime(rs.getTimestamp("blocked_time").toLocalDateTime());
                blockedSlot.setReason(rs.getString("reason"));
                blockedSlot.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                blockedSlots.add(blockedSlot);
            }
        } catch (SQLException e) {
            System.err.println("Error getting blocked slots: " + e.getMessage());
        }
        
        return blockedSlots;
    }
    
    public boolean deleteBlockedSlot(int blockedSlotId) {
        String sql = "DELETE FROM blocked_slots WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, blockedSlotId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting blocked slot: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isTimeSlotBlocked(int dentistId, LocalDateTime appointmentTime) {
        String sql = "SELECT COUNT(*) FROM blocked_slots WHERE dentist_id = ? AND blocked_time = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dentistId);
            stmt.setTimestamp(2, Timestamp.valueOf(appointmentTime));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if time slot is blocked: " + e.getMessage());
        }
        
        return false;
    }
} 