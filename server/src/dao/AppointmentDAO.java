package dao;

import database.DatabaseConnector;
import model.Appointment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private DatabaseConnector dbConnector;
    
    public AppointmentDAO() {
        this.dbConnector = new DatabaseConnector();
    }
    
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, dentist_id, appointment_time, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDentistId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            stmt.setString(4, appointment.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            return false;
        }
    }
    
    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_time";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setDentistId(rs.getInt("dentist_id"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
                appointment.setStatus(rs.getString("status"));
                appointment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by patient: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByDentistId(int dentistId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE dentist_id = ? ORDER BY appointment_time";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setDentistId(rs.getInt("dentist_id"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
                appointment.setStatus(rs.getString("status"));
                appointment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by dentist: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isTimeSlotAvailable(int dentistId, LocalDateTime appointmentTime) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE dentist_id = ? AND appointment_time = ? AND status != 'cancelled'";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dentistId);
            stmt.setTimestamp(2, Timestamp.valueOf(appointmentTime));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking time slot availability: " + e.getMessage());
        }
        
        return false;
    }
} 