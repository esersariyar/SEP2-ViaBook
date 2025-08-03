package service;

import dao.AppointmentDAO;
import model.Appointment;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
    private AppointmentDAO appointmentDAO;
    
    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }
    
    public boolean createAppointment(int patientId, int dentistId, LocalDateTime appointmentTime) {
        if (patientId <= 0 || dentistId <= 0 || appointmentTime == null) {
            return false;
        }
        
        // Check if appointment time is in the future
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Check if time slot is available
        if (!appointmentDAO.isTimeSlotAvailable(dentistId, appointmentTime)) {
            return false;
        }
        
        Appointment appointment = new Appointment(patientId, dentistId, appointmentTime);
        return appointmentDAO.createAppointment(appointment);
    }
    
    public List<Appointment> getPatientAppointments(int patientId) {
        return appointmentDAO.getAppointmentsByPatientId(patientId);
    }
    
    public List<Appointment> getDentistAppointments(int dentistId) {
        return appointmentDAO.getAppointmentsByDentistId(dentistId);
    }
    
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        if (appointmentId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        // Validate status
        if (!"pending".equals(status) && !"approved".equals(status) && !"cancelled".equals(status)) {
            return false;
        }
        
        return appointmentDAO.updateAppointmentStatus(appointmentId, status);
    }
    
    public boolean updateAppointmentStatusByPatient(int appointmentId, String status) {
        if (appointmentId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        // Validate status
        if (!"pending".equals(status) && !"approved".equals(status) && !"cancelled".equals(status)) {
            return false;
        }
        
        // If cancelling appointment, check 24-hour rule for patients
        if ("cancelled".equals(status)) {
            Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
            if (appointment != null) {
                LocalDateTime appointmentTime = appointment.getAppointmentTime();
                LocalDateTime now = LocalDateTime.now();
                
                if (appointmentTime.isBefore(now.plusHours(24))) {
                    return false; // Cannot cancel within 24 hours
                }
            }
        }
        
        return appointmentDAO.updateAppointmentStatus(appointmentId, status);
    }
    
    public boolean isTimeSlotAvailable(int dentistId, LocalDateTime appointmentTime) {
        if (dentistId <= 0 || appointmentTime == null) {
            return false;
        }
        
        return appointmentDAO.isTimeSlotAvailable(dentistId, appointmentTime);
    }

    public List<Appointment> getPastAppointmentsByPatientId(int patientId) {
        return appointmentDAO.getPastAppointmentsByPatientId(patientId);
    }

    public List<Appointment> getPastAppointmentsByDentistId(int dentistId) {
        return appointmentDAO.getPastAppointmentsByDentistId(dentistId);
    }
} 