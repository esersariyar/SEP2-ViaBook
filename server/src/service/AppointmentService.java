import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
    private AppointmentDAO appointmentDAO;
    
    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }
    
    public boolean createAppointment(int userId, String title, String description, 
                                   LocalDateTime startTime, LocalDateTime endTime) {
        if (title == null || title.trim().isEmpty() || 
            startTime == null || endTime == null || 
            startTime.isAfter(endTime)) {
            return false;
        }
        
        Appointment appointment = new Appointment(userId, title, description, startTime, endTime);
        return appointmentDAO.createAppointment(appointment);
    }
    
    public List<Appointment> getUserAppointments(int userId) {
        return appointmentDAO.getAppointmentsByUser(userId);
    }
    
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        // TODO: Get appointment by ID, update status, and save
        return false;
    }
    
    public boolean isValidTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null && 
               startTime.isBefore(endTime) && 
               startTime.isAfter(LocalDateTime.now());
    }
} 