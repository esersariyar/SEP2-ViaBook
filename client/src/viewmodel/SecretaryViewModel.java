package viewmodel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.User;
import service.RMIClient;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SecretaryViewModel {
    private final RMIClient rmiClient;
    
    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty email;
    private final StringProperty selectedStatus;
    private final StringProperty errorMessage;
    
    private final ObservableList<Appointment> pendingAppointments;
    private final ObservableList<Appointment> allAppointments;
    private final ObservableList<User> dentists;
    private final ObservableList<String> statusOptions;
    
    private List<User> cachedUsers;
    private Map<Integer, User> userMap;
    
    public SecretaryViewModel() {
        this.rmiClient = new RMIClient();
        
        this.name = new SimpleStringProperty("");
        this.surname = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.selectedStatus = new SimpleStringProperty("All");
        this.errorMessage = new SimpleStringProperty("");
        
        this.pendingAppointments = FXCollections.observableArrayList();
        this.allAppointments = FXCollections.observableArrayList();
        this.dentists = FXCollections.observableArrayList();
        this.statusOptions = FXCollections.observableArrayList("All", "Pending", "Approved", "Cancelled");
        
        this.cachedUsers = null;
        this.userMap = null;
    }
    
    public StringProperty nameProperty() { return name; }
    public StringProperty surnameProperty() { return surname; }
    public StringProperty emailProperty() { return email; }
    public StringProperty selectedStatusProperty() { return selectedStatus; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    
    public ObservableList<Appointment> getPendingAppointments() { return pendingAppointments; }
    public ObservableList<Appointment> getAllAppointments() { return allAppointments; }
    public ObservableList<User> getDentists() { return dentists; }
    public ObservableList<String> getStatusOptions() { return statusOptions; }
    
    public void setUser(User user) {
        if (user != null) {
            name.set(user.getFirstName());
            surname.set(user.getLastName());
            email.set(user.getEmail());
        }
    }
    
    public void loadData() {
        cacheUsers();
        loadAllAppointments();
        loadDentists();
    }
    
    private void cacheUsers() {
        cachedUsers = rmiClient.getAllUsers();
        userMap = new HashMap<>();
        for (User user : cachedUsers) {
            userMap.put(user.getId(), user);
        }
    }
    
    private User getUserById(int userId) {
        if (userMap == null) {
            cacheUsers();
        }
        return userMap.get(userId);
    }
    
    private void loadAllAppointments() {
        if (userMap == null) {
            cacheUsers();
        }
        allAppointments.clear();
        for (User user : cachedUsers) {
            if ("dentist".equals(user.getRole())) {
                List<Appointment> dentistAppointments = rmiClient.getDentistAppointments(user.getId());
                allAppointments.addAll(dentistAppointments);
            }
        }
        loadPendingAppointments();
    }
    
    private void loadPendingAppointments() {
        pendingAppointments.clear();
        for (Appointment appointment : allAppointments) {
            if ("pending".equals(appointment.getStatus())) {
                pendingAppointments.add(appointment);
            }
        }
    }
    
    private void loadDentists() {
        if (userMap == null) {
            cacheUsers();
        }
        dentists.clear();
        for (User user : cachedUsers) {
            if ("dentist".equals(user.getRole())) {
                dentists.add(user);
            }
        }
    }
    
    public boolean approveAppointment(Appointment appointment) {
        if (appointment == null) {
            errorMessage.set("Please select an appointment to approve");
            return false;
        }
        
        if (rmiClient.updateAppointmentStatus(appointment.getId(), "approved")) {
            errorMessage.set("Appointment approved successfully");
            loadAllAppointments();
            return true;
        } else {
            errorMessage.set("Failed to approve appointment");
            return false;
        }
    }
    
    public boolean rejectAppointment(Appointment appointment) {
        if (appointment == null) {
            errorMessage.set("Please select an appointment to reject");
            return false;
        }
        
        if (rmiClient.updateAppointmentStatus(appointment.getId(), "cancelled")) {
            errorMessage.set("Appointment rejected successfully");
            loadAllAppointments();
            return true;
        } else {
            errorMessage.set("Failed to reject appointment");
            return false;
        }
    }
    
    public boolean createDentist(String firstName, String lastName, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.set("All fields are required");
            return false;
        }
        
        if (rmiClient.registerUser(password, email, firstName, lastName, "dentist")) {
            errorMessage.set("Dentist created successfully");
            userMap = null;
            loadDentists();
            return true;
        } else {
            errorMessage.set("Failed to create dentist");
            return false;
        }
    }
    
    public boolean deleteDentist(User dentist) {
        if (rmiClient.deleteUser(dentist.getId())) {
            errorMessage.set("Dentist deleted successfully");
            userMap = null;
            loadDentists();
            return true;
        } else {
            errorMessage.set("Failed to delete dentist");
            return false;
        }
    }
    
    public void refreshData() {
        loadData();
        selectedStatus.set("All");
        errorMessage.set("Appointments refreshed");
    }
    
    public void filterAppointments(String status) {
        if (status != null && !"All".equals(status)) {
            String statusLower = status.toLowerCase();
            ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
            
            for (Appointment appointment : allAppointments) {
                if (statusLower.equals(appointment.getStatus())) {
                    filteredAppointments.add(appointment);
                }
            }
            allAppointments.clear();
            allAppointments.addAll(filteredAppointments);
        } else {
            loadAllAppointments();
        }
    }
    
    public String getFormattedDate(Appointment appointment) {
        return appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public String getFormattedTime(Appointment appointment) {
        return appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    public String getPatientName(Appointment appointment) {
        User patient = getUserById(appointment.getPatientId());
        return patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown";
    }
    
    public String getDentistName(Appointment appointment) {
        User dentist = getUserById(appointment.getDentistId());
        return dentist != null ? "Dr. " + dentist.getFirstName() + " " + dentist.getLastName() : "Unknown";
    }
    
    public String getFormattedStatus(Appointment appointment) {
        String status = appointment.getStatus();
        return status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown";
    }
    
    public void clearError() {
        errorMessage.set("");
    }
} 