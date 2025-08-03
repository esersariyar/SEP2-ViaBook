package rmi;

import service.UserService;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import model.BlockedSlot;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;

public class UserServiceImpl extends UnicastRemoteObject implements UserServiceInterface {
    private UserService userService;
    
    public UserServiceImpl() throws RemoteException {
        super();
        this.userService = new UserService();
    }
    
    @Override
    public User authenticateUser(String email, String password) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Login attempt for email: " + email);
            
            User user = userService.authenticateUser(email, password);
            if (user != null) {
                System.out.println("ViaBook Server: Login successful for " + email);
                return user;
            }
            
            System.out.println("ViaBook Server: Login failed for " + email);
            return null;
            
        } catch (Exception e) {
            System.err.println("ViaBook Server: Authentication error: " + e.getMessage());
            throw new RemoteException("Authentication failed", e);
        }
    }
    
    @Override
    public boolean registerUser(String password, String email, String firstName, String lastName, String role) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Registration attempt for email: " + email);
            return userService.registerUser(password, email, firstName, lastName, role);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Registration error: " + e.getMessage());
            throw new RemoteException("Registration failed", e);
        }
    }
    
    @Override
    public boolean isValidUser(String email) throws RemoteException {
        try {
            return userService.isValidUser(email);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Validation error: " + e.getMessage());
            throw new RemoteException("User validation failed", e);
        }
    }
    
    @Override
    public boolean updateUser(User user) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update attempt for user: " + user.getEmail());
            return userService.updateUser(user);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update error: " + e.getMessage());
            throw new RemoteException("User update failed", e);
        }
    }
    
    @Override
    public List<User> getAllUsers() throws RemoteException {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get all users error: " + e.getMessage());
            throw new RemoteException("Get all users failed", e);
        }
    }
    
    @Override
    public boolean deleteUser(int userId) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Delete attempt for user ID: " + userId);
            return userService.deleteUser(userId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Delete error: " + e.getMessage());
            throw new RemoteException("User delete failed", e);
        }
    }
    
    @Override
    public DentistProfile getDentistProfile(int userId) throws RemoteException {
        try {
            return userService.getDentistProfile(userId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get dentist profile error: " + e.getMessage());
            throw new RemoteException("Get dentist profile failed", e);
        }
    }
    
    @Override
    public boolean updateDentistProfile(DentistProfile profile) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update dentist profile attempt for user ID: " + profile.getUserId());
            return userService.updateDentistProfile(profile);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update dentist profile error: " + e.getMessage());
            throw new RemoteException("Update dentist profile failed", e);
        }
    }
    
    @Override
    public List<WorkingHours> getWorkingHours(int dentistId) throws RemoteException {
        try {
            return userService.getWorkingHours(dentistId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get working hours error: " + e.getMessage());
            throw new RemoteException("Get working hours failed", e);
        }
    }
    
    @Override
    public boolean addWorkingHours(WorkingHours workingHours) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Add working hours attempt for dentist ID: " + workingHours.getDentistId());
            return userService.addWorkingHours(workingHours);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Add working hours error: " + e.getMessage());
            throw new RemoteException("Add working hours failed", e);
        }
    }
    
    @Override
    public boolean updateWorkingHours(WorkingHours workingHours) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update working hours attempt for dentist ID: " + workingHours.getDentistId());
            return userService.updateWorkingHours(workingHours);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update working hours error: " + e.getMessage());
            throw new RemoteException("Update working hours failed", e);
        }
    }
    
    @Override
    public boolean deleteWorkingHours(int dentistId, String dayOfWeek) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Delete working hours attempt for dentist ID: " + dentistId + ", day: " + dayOfWeek);
            return userService.deleteWorkingHours(dentistId, dayOfWeek);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Delete working hours error: " + e.getMessage());
            throw new RemoteException("Delete working hours failed", e);
        }
    }
    
    @Override
    public boolean createAppointment(int patientId, int dentistId, LocalDateTime appointmentTime) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Create appointment attempt - Patient ID: " + patientId + ", Dentist ID: " + dentistId);
            return userService.createAppointment(patientId, dentistId, appointmentTime);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Create appointment error: " + e.getMessage());
            throw new RemoteException("Create appointment failed", e);
        }
    }
    
    @Override
    public List<Appointment> getPatientAppointments(int patientId) throws RemoteException {
        try {
            return userService.getPatientAppointments(patientId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get patient appointments error: " + e.getMessage());
            throw new RemoteException("Get patient appointments failed", e);
        }
    }
    
    @Override
    public List<Appointment> getDentistAppointments(int dentistId) throws RemoteException {
        try {
            return userService.getDentistAppointments(dentistId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get dentist appointments error: " + e.getMessage());
            throw new RemoteException("Get dentist appointments failed", e);
        }
    }
    
    @Override
    public boolean updateAppointmentStatus(int appointmentId, String status) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update appointment status attempt - ID: " + appointmentId + ", Status: " + status);
            return userService.updateAppointmentStatus(appointmentId, status);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update appointment status error: " + e.getMessage());
            throw new RemoteException("Update appointment status failed", e);
        }
    }
    
    @Override
    public boolean updateAppointmentStatusByPatient(int appointmentId, String status) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Update appointment status by patient attempt - ID: " + appointmentId + ", Status: " + status);
            return userService.updateAppointmentStatusByPatient(appointmentId, status);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Update appointment status by patient error: " + e.getMessage());
            throw new RemoteException("Update appointment status by patient failed", e);
        }
    }
    
    @Override
    public boolean isTimeSlotAvailable(int dentistId, LocalDateTime appointmentTime) throws RemoteException {
        try {
            return userService.isTimeSlotAvailable(dentistId, appointmentTime);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Check time slot availability error: " + e.getMessage());
            throw new RemoteException("Check time slot availability failed", e);
        }
    }
    
    @Override
    public boolean createBlockedSlot(BlockedSlot blockedSlot) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Create blocked slot attempt for dentist ID: " + blockedSlot.getDentistId());
            return userService.createBlockedSlot(blockedSlot);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Create blocked slot error: " + e.getMessage());
            throw new RemoteException("Create blocked slot failed", e);
        }
    }
    
    @Override
    public List<BlockedSlot> getBlockedSlots(int dentistId) throws RemoteException {
        try {
            return userService.getBlockedSlots(dentistId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get blocked slots error: " + e.getMessage());
            throw new RemoteException("Get blocked slots failed", e);
        }
    }
    
    @Override
    public boolean deleteBlockedSlot(int blockedSlotId) throws RemoteException {
        try {
            System.out.println("ViaBook Server: Delete blocked slot attempt - ID: " + blockedSlotId);
            return userService.deleteBlockedSlot(blockedSlotId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Delete blocked slot error: " + e.getMessage());
            throw new RemoteException("Delete blocked slot failed", e);
        }
    }

    @Override
    public List<Appointment> getPastAppointmentsByPatientId(int patientId) throws RemoteException {
        try {
            return userService.getPastAppointmentsByPatientId(patientId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get past patient appointments error: " + e.getMessage());
            throw new RemoteException("Get past patient appointments failed", e);
        }
    }

    @Override
    public List<Appointment> getPastAppointmentsByDentistId(int dentistId) throws RemoteException {
        try {
            return userService.getPastAppointmentsByDentistId(dentistId);
        } catch (Exception e) {
            System.err.println("ViaBook Server: Get past dentist appointments error: " + e.getMessage());
            throw new RemoteException("Get past dentist appointments failed", e);
        }
    }
} 