package rmi;

import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import model.BlockedSlot;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.List;

public interface UserServiceInterface extends Remote {
    
    User authenticateUser(String email, String password) throws RemoteException;
    
    boolean registerUser(String password, String email, String firstName, String lastName, String role) throws RemoteException;
    
    boolean isValidUser(String email) throws RemoteException;
    
    boolean updateUser(User user) throws RemoteException;
    
    List<User> getAllUsers() throws RemoteException;
    
    boolean deleteUser(int userId) throws RemoteException;
    
    DentistProfile getDentistProfile(int userId) throws RemoteException;
    
    boolean updateDentistProfile(DentistProfile profile) throws RemoteException;
    
    List<WorkingHours> getWorkingHours(int dentistId) throws RemoteException;
    
    boolean addWorkingHours(WorkingHours workingHours) throws RemoteException;
    
    boolean updateWorkingHours(WorkingHours workingHours) throws RemoteException;
    
    boolean deleteWorkingHours(int dentistId, String dayOfWeek) throws RemoteException;
    
    boolean createAppointment(int patientId, int dentistId, LocalDateTime appointmentTime) throws RemoteException;
    
    List<Appointment> getPatientAppointments(int patientId) throws RemoteException;
    
    List<Appointment> getDentistAppointments(int dentistId) throws RemoteException;
    
    boolean updateAppointmentStatus(int appointmentId, String status) throws RemoteException;
    
    boolean updateAppointmentStatusByPatient(int appointmentId, String status) throws RemoteException;
    
    boolean isTimeSlotAvailable(int dentistId, LocalDateTime appointmentTime) throws RemoteException;
    
    boolean createBlockedSlot(BlockedSlot blockedSlot) throws RemoteException;
    
    List<BlockedSlot> getBlockedSlots(int dentistId) throws RemoteException;
    
    boolean deleteBlockedSlot(int blockedSlotId) throws RemoteException;
    
    List<Appointment> getPastAppointmentsByPatientId(int patientId) throws RemoteException;
    List<Appointment> getPastAppointmentsByDentistId(int dentistId) throws RemoteException;
} 