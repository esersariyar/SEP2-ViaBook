package dao;

import model.Appointment;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

public class AppointmentDAO {
    
    public List<Appointment> getAppointmentsByUser(int userId) throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public boolean createAppointment(Appointment appointment) throws SQLException {
        // TODO: Implement method
        return false;
    }
    
    public boolean updateAppointment(Appointment appointment) throws SQLException {
        // TODO: Implement method
        return false;
    }
    
    public boolean deleteAppointment(int appointmentId) throws SQLException {
        // TODO: Implement method
        return false;
    }
    
    public List<Appointment> getAllAppointments() throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        // TODO: Implement method
        return null;
    }
    
    public List<Appointment> getAvailableSlots(LocalDateTime date) throws SQLException {
        // TODO: Implement method
        return null;
    }
} 