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
        String sql = "INSERT INTO appointments (user_id, title, description, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointment.getUserId());
            stmt.setString(2, appointment.getTitle());
            stmt.setString(3, appointment.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(appointment.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(appointment.getEndTime()));
            stmt.setString(6, appointment.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            return false;
        }
    }
    
    public List<Appointment> getAppointmentsByUser(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE user_id = ? ORDER BY start_time";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setUserId(rs.getInt("user_id"));
                appointment.setTitle(rs.getString("title"));
                appointment.setDescription(rs.getString("description"));
                appointment.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                appointment.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                appointment.setStatus(rs.getString("status"));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET title = ?, description = ?, start_time = ?, end_time = ?, status = ? WHERE appointment_id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointment.getTitle());
            stmt.setString(2, appointment.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(appointment.getEndTime()));
            stmt.setString(5, appointment.getStatus());
            stmt.setInt(6, appointment.getAppointmentId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }
} 