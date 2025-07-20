package viewmodel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.BlockedSlot;
import service.RMIClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DentistViewModel {
    private final RMIClient rmiClient;
    
    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty email;
    private final StringProperty specialization;
    private final StringProperty description;
    private final StringProperty errorMessage;
    
    private final ObservableList<Appointment> upcomingAppointments;
    private final ObservableList<WorkingHours> workingHoursList;
    private final ObservableList<String> availableSlots;
    private final ObservableList<String> dayOptions;
    
    private User currentUser;
    private WorkingHours selectedWorkingHours;
    private LocalDate selectedSlotDate;
    private String selectedSlot;
    private BlockedSlot selectedBlockedSlot;
    private Map<String, Integer> blockedSlotMap;
    private List<User> cachedUsers;
    private Map<Integer, User> userMap;
    
    public DentistViewModel() {
        this.rmiClient = new RMIClient();
        
        this.name = new SimpleStringProperty("");
        this.surname = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.specialization = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.errorMessage = new SimpleStringProperty("");
        
        this.upcomingAppointments = FXCollections.observableArrayList();
        this.workingHoursList = FXCollections.observableArrayList();
        this.availableSlots = FXCollections.observableArrayList();
        this.dayOptions = FXCollections.observableArrayList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        );
        
        this.currentUser = null;
        this.selectedWorkingHours = null;
        this.selectedSlotDate = null;
        this.selectedSlot = null;
        this.selectedBlockedSlot = null;
        this.blockedSlotMap = new HashMap<>();
        this.cachedUsers = null;
        this.userMap = null;
    }
    
    public StringProperty nameProperty() { return name; }
    public StringProperty surnameProperty() { return surname; }
    public StringProperty emailProperty() { return email; }
    public StringProperty specializationProperty() { return specialization; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    
    public ObservableList<Appointment> getUpcomingAppointments() { return upcomingAppointments; }
    public ObservableList<WorkingHours> getWorkingHoursList() { return workingHoursList; }
    public ObservableList<String> getAvailableSlots() { return availableSlots; }
    public ObservableList<String> getDayOptions() { return dayOptions; }
    
    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            name.set(user.getFirstName());
            surname.set(user.getLastName());
            email.set(user.getEmail());
        }
        loadData();
    }
    
    public void loadData() {
        loadDentistProfile();
        loadWorkingHours();
        loadUpcomingAppointments();
        cacheUsers();
    }
    
    private void loadDentistProfile() {
        if (currentUser != null) {
            DentistProfile profile = rmiClient.getDentistProfile(currentUser.getId());
            if (profile != null) {
                specialization.set(profile.getSpecialization() != null ? profile.getSpecialization() : "");
                description.set(profile.getDescription() != null ? profile.getDescription() : "");
            }
        }
    }
    
    private void loadWorkingHours() {
        if (currentUser != null) {
            List<WorkingHours> hours = rmiClient.getWorkingHours(currentUser.getId());
            workingHoursList.clear();
            workingHoursList.addAll(hours);
        }
    }
    
    private void loadUpcomingAppointments() {
        if (currentUser != null) {
            List<Appointment> appointments = rmiClient.getDentistAppointments(currentUser.getId());
            upcomingAppointments.clear();
            
            for (Appointment appointment : appointments) {
                if ("approved".equals(appointment.getStatus()) && 
                    appointment.getAppointmentTime().isAfter(LocalDateTime.now())) {
                    upcomingAppointments.add(appointment);
                }
            }
        }
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
    
    public boolean updateProfile(String specialization, String description) {
        if (currentUser == null) {
            errorMessage.set("User not logged in");
            return false;
        }
        
        DentistProfile profile = new DentistProfile();
        profile.setUserId(currentUser.getId());
        profile.setSpecialization(specialization);
        profile.setDescription(description);
        
        if (rmiClient.updateDentistProfile(profile)) {
            errorMessage.set("Profile updated successfully");
            this.specialization.set(specialization);
            this.description.set(description);
            return true;
        } else {
            errorMessage.set("Failed to update profile");
            return false;
        }
    }
    
    public boolean addWorkingHours(String day, String startTime, String endTime) {
        if (day.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            errorMessage.set("All fields are required");
            return false;
        }
        
        try {
            LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
            
            if (start.isAfter(end)) {
                errorMessage.set("Start time must be before end time");
                return false;
            }
            
            WorkingHours workingHours = new WorkingHours();
            workingHours.setDentistId(currentUser.getId());
            workingHours.setDayOfWeek(day);
            workingHours.setStartTime(start);
            workingHours.setEndTime(end);
            
            if (rmiClient.addWorkingHours(workingHours)) {
                errorMessage.set("Working hours added successfully");
                loadWorkingHours();
                return true;
            } else {
                errorMessage.set("Failed to add working hours");
                return false;
            }
        } catch (DateTimeParseException e) {
            errorMessage.set("Invalid time format. Use HH:mm");
            return false;
        }
    }
    
    public boolean updateWorkingHours(String day, String startTime, String endTime) {
        if (selectedWorkingHours == null) {
            errorMessage.set("Please select working hours to update");
            return false;
        }
        
        if (day.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            errorMessage.set("All fields are required");
            return false;
        }
        
        try {
            LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
            
            if (start.isAfter(end)) {
                errorMessage.set("Start time must be before end time");
                return false;
            }
            
            selectedWorkingHours.setDayOfWeek(day);
            selectedWorkingHours.setStartTime(start);
            selectedWorkingHours.setEndTime(end);
            
            if (rmiClient.updateWorkingHours(selectedWorkingHours)) {
                errorMessage.set("Working hours updated successfully");
                loadWorkingHours();
                return true;
            } else {
                errorMessage.set("Failed to update working hours");
                return false;
            }
        } catch (DateTimeParseException e) {
            errorMessage.set("Invalid time format. Use HH:mm");
            return false;
        }
    }
    
    public boolean deleteWorkingHours() {
        if (selectedWorkingHours == null) {
            errorMessage.set("Please select working hours to delete");
            return false;
        }
        
        if (rmiClient.deleteWorkingHours(currentUser.getId(), selectedWorkingHours.getDayOfWeek())) {
            errorMessage.set("Working hours deleted successfully");
            loadWorkingHours();
            return true;
        } else {
            errorMessage.set("Failed to delete working hours");
            return false;
        }
    }
    
    public void selectWorkingHours(WorkingHours workingHours) {
        this.selectedWorkingHours = workingHours;
    }
    
    public void selectSlotDate(LocalDate date) {
        this.selectedSlotDate = date;
        loadAvailableSlotsForDate(date);
    }
    
    public void selectSlot(String slot) {
        this.selectedSlot = slot;
    }
    
    private void loadAvailableSlotsForDate(LocalDate date) {
        availableSlots.clear();
        blockedSlotMap.clear();
        
        if (currentUser == null || date == null) {
            return;
        }
        
        String dayOfWeek = date.getDayOfWeek().toString();
        List<WorkingHours> workingHours = rmiClient.getWorkingHours(currentUser.getId());
        
        for (WorkingHours hours : workingHours) {
            if (dayOfWeek.equalsIgnoreCase(hours.getDayOfWeek())) {
                LocalTime start = hours.getStartTime();
                LocalTime end = hours.getEndTime();
                
                if (start != null && end != null) {
                    LocalTime current = start;
                    int slotIndex = 0;
                    
                    while (!current.isAfter(end.minusMinutes(30))) {
                        LocalDateTime slotDateTime = LocalDateTime.of(date, current);
                        
                        if (slotDateTime.isAfter(LocalDateTime.now())) {
                            List<Appointment> existingAppointments = rmiClient.getDentistAppointments(currentUser.getId());
                            boolean isSlotAvailable = true;
                            
                            for (Appointment appointment : existingAppointments) {
                                if (appointment.getAppointmentTime().equals(slotDateTime) && 
                                    "approved".equals(appointment.getStatus())) {
                                    isSlotAvailable = false;
                                    break;
                                }
                            }
                            
                            if (isSlotAvailable) {
                                String slotTime = current.format(DateTimeFormatter.ofPattern("HH:mm"));
                                availableSlots.add(slotTime);
                                blockedSlotMap.put(slotTime, slotIndex);
                            }
                        }
                        current = current.plusMinutes(30);
                        slotIndex++;
                    }
                }
                break;
            }
        }
    }
    
    public boolean blockSlot() {
        if (selectedSlot == null) {
            errorMessage.set("Please select a time slot to block");
            return false;
        }
        
        if (currentUser == null || selectedSlotDate == null) {
            errorMessage.set("Invalid selection");
            return false;
        }
        
        LocalTime time = LocalTime.parse(selectedSlot, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime slotDateTime = LocalDateTime.of(selectedSlotDate, time);
        
        BlockedSlot blockedSlot = new BlockedSlot();
        blockedSlot.setDentistId(currentUser.getId());
        blockedSlot.setBlockedTime(slotDateTime);
        
        if (rmiClient.createBlockedSlot(blockedSlot)) {
            errorMessage.set("Time slot blocked successfully");
            loadAvailableSlotsForDate(selectedSlotDate);
            return true;
        } else {
            errorMessage.set("Failed to block time slot");
            return false;
        }
    }
    
    public boolean unblockSlot() {
        if (selectedBlockedSlot == null) {
            errorMessage.set("Please select a blocked slot to unblock");
            return false;
        }
        
        if (rmiClient.deleteBlockedSlot(selectedBlockedSlot.getId())) {
            errorMessage.set("Time slot unblocked successfully");
            loadAvailableSlotsForDate(selectedSlotDate);
            return true;
        } else {
            errorMessage.set("Failed to unblock time slot");
            return false;
        }
    }
    
    public void selectBlockedSlot(BlockedSlot blockedSlot) {
        this.selectedBlockedSlot = blockedSlot;
    }
    
    public void refreshAppointments() {
        loadUpcomingAppointments();
        errorMessage.set("Appointments refreshed");
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
    
    public String getFormattedStatus(Appointment appointment) {
        String status = appointment.getStatus();
        return status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown";
    }
    
    public String getFormattedWorkingHoursStart(WorkingHours workingHours) {
        LocalTime startTime = workingHours.getStartTime();
        return startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
    
    public String getFormattedWorkingHoursEnd(WorkingHours workingHours) {
        LocalTime endTime = workingHours.getEndTime();
        return endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
    
    public void clearError() {
        errorMessage.set("");
    }
} 