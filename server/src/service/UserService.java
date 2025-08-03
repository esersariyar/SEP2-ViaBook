package service;

import dao.UserDAO;
import dao.DentistProfileDAO;
import dao.WorkingHoursDAO;
import dao.BlockedSlotDAO;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import model.BlockedSlot;
import java.time.LocalDateTime;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private DentistProfileDAO dentistProfileDAO;
    private WorkingHoursDAO workingHoursDAO;
    private BlockedSlotDAO blockedSlotDAO;
    private AppointmentService appointmentService;
    
    public UserService() {
        this.userDAO = new UserDAO();
        this.dentistProfileDAO = new DentistProfileDAO();
        this.workingHoursDAO = new WorkingHoursDAO();
        this.blockedSlotDAO = new BlockedSlotDAO();
        this.appointmentService = new AppointmentService();
    }
    
    public User authenticateUser(String email, String password) {
        if (email == null || password == null ||
            email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        
        return userDAO.validateLogin(email, password);
    }
    
    public boolean registerUser(String password, String email, String firstName, String lastName, String role) {
        if (password == null || email == null || firstName == null || lastName == null || role == null ||
            password.trim().isEmpty() || email.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty() || role.trim().isEmpty()) {
            return false;
        }
        
        User newUser = new User(password, email, firstName, lastName, role);
        return userDAO.createUser(newUser);
    }
    
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    public boolean isValidUser(String email) {
        return email != null && !email.trim().isEmpty();
    }
    
    public boolean updateUser(User user) {
        if (user == null || user.getEmail() == null || user.getFirstName() == null || user.getLastName() == null ||
            user.getEmail().trim().isEmpty() || user.getFirstName().trim().isEmpty() || user.getLastName().trim().isEmpty()) {
            return false;
        }
        
        return userDAO.updateUser(user);
    }
    
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
    
    public DentistProfile getDentistProfile(int userId) {
        return dentistProfileDAO.getProfileByUserId(userId);
    }
    
    public boolean updateDentistProfile(DentistProfile profile) {
        if (profile == null || profile.getUserId() <= 0) {
            return false;
        }
        
        return dentistProfileDAO.createOrUpdateProfile(profile);
    }
    
    public List<WorkingHours> getWorkingHours(int dentistId) {
        return workingHoursDAO.getWorkingHoursByDentistId(dentistId);
    }
    
    public boolean addWorkingHours(WorkingHours workingHours) {
        if (workingHours == null || workingHours.getDentistId() <= 0 || 
            workingHours.getDayOfWeek() == null || workingHours.getDayOfWeek().trim().isEmpty() ||
            workingHours.getStartTime() == null || workingHours.getEndTime() == null) {
            return false;
        }
        
        return workingHoursDAO.addWorkingHours(workingHours);
    }
    
    public boolean updateWorkingHours(WorkingHours workingHours) {
        if (workingHours == null || workingHours.getDentistId() <= 0 || 
            workingHours.getDayOfWeek() == null || workingHours.getDayOfWeek().trim().isEmpty() ||
            workingHours.getStartTime() == null || workingHours.getEndTime() == null) {
            return false;
        }
        
        return workingHoursDAO.updateWorkingHours(workingHours);
    }
    
    public boolean deleteWorkingHours(int dentistId, String dayOfWeek) {
        if (dentistId <= 0 || dayOfWeek == null || dayOfWeek.trim().isEmpty()) {
            return false;
        }
        
        return workingHoursDAO.deleteWorkingHours(dentistId, dayOfWeek);
    }
    
    public boolean createAppointment(int patientId, int dentistId, LocalDateTime appointmentTime) {
        return appointmentService.createAppointment(patientId, dentistId, appointmentTime);
    }
    
    public List<Appointment> getPatientAppointments(int patientId) {
        return appointmentService.getPatientAppointments(patientId);
    }
    
    public List<Appointment> getDentistAppointments(int dentistId) {
        return appointmentService.getDentistAppointments(dentistId);
    }
    
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return appointmentService.updateAppointmentStatus(appointmentId, status);
    }
    
    public boolean updateAppointmentStatusByPatient(int appointmentId, String status) {
        return appointmentService.updateAppointmentStatusByPatient(appointmentId, status);
    }
    
    public boolean isTimeSlotAvailable(int dentistId, LocalDateTime appointmentTime) {
        // Check if the time slot is blocked
        if (blockedSlotDAO.isTimeSlotBlocked(dentistId, appointmentTime)) {
            return false;
        }
        
        return appointmentService.isTimeSlotAvailable(dentistId, appointmentTime);
    }
    
    public boolean createBlockedSlot(BlockedSlot blockedSlot) {
        if (blockedSlot == null || blockedSlot.getDentistId() <= 0 || blockedSlot.getBlockedTime() == null) {
            return false;
        }
        
        return blockedSlotDAO.createBlockedSlot(blockedSlot);
    }
    
    public List<BlockedSlot> getBlockedSlots(int dentistId) {
        return blockedSlotDAO.getBlockedSlotsByDentistId(dentistId);
    }
    
    public boolean deleteBlockedSlot(int blockedSlotId) {
        return blockedSlotDAO.deleteBlockedSlot(blockedSlotId);
    }

    public List<Appointment> getPastAppointmentsByPatientId(int patientId) {
        return appointmentService.getPastAppointmentsByPatientId(patientId);
    }

    public List<Appointment> getPastAppointmentsByDentistId(int dentistId) {
        return appointmentService.getPastAppointmentsByDentistId(dentistId);
    }
} 