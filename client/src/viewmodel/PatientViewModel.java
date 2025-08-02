package viewmodel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import service.RMIClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class PatientViewModel {
    private final RMIClient rmiClient;
    
    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty email;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty userEmail;
    private final StringProperty errorMessage;
    private final StringProperty dentistProfile;
    
    private final ObservableList<Appointment> upcomingAppointments;
    private final ObservableList<Appointment> pastAppointments;
    private final ObservableList<User> dentists;
    private final ObservableList<String> availableTimeSlots;
    
    private User currentUser;
    private User selectedDentist;
    private LocalDate selectedDate;
    private String selectedTimeSlot;
    
    public PatientViewModel() {
        this.rmiClient = new RMIClient();
        
        this.name = new SimpleStringProperty("");
        this.surname = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.firstName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.userEmail = new SimpleStringProperty("");
        this.errorMessage = new SimpleStringProperty("");
        this.dentistProfile = new SimpleStringProperty("");
        
        this.upcomingAppointments = FXCollections.observableArrayList();
        this.pastAppointments = FXCollections.observableArrayList();
        this.dentists = FXCollections.observableArrayList();
        this.availableTimeSlots = FXCollections.observableArrayList();
        
        this.currentUser = null;
        this.selectedDentist = null;
        this.selectedDate = null;
        this.selectedTimeSlot = null;
    }
    
    public StringProperty nameProperty() { return name; }
    public StringProperty surnameProperty() { return surname; }
    public StringProperty emailProperty() { return email; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty userEmailProperty() { return userEmail; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty dentistProfileProperty() { return dentistProfile; }
    
    public ObservableList<Appointment> getUpcomingAppointments() { return upcomingAppointments; }
    public ObservableList<Appointment> getPastAppointments() { return pastAppointments; }
    public ObservableList<User> getDentists() { return dentists; }
    public ObservableList<String> getAvailableTimeSlots() { return availableTimeSlots; }
    
    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            name.set(user.getFirstName());
            surname.set(user.getLastName());
            email.set(user.getEmail());
            firstName.set(user.getFirstName());
            lastName.set(user.getLastName());
            userEmail.set(user.getEmail());
        }
        loadData();
    }
    
    public void loadData() {
        loadDentists();
        loadUpcomingAppointments();
        loadPastAppointments();
    }
    
    private void loadDentists() {
        List<User> allUsers = rmiClient.getAllUsers();
        dentists.clear();
        
        for (User user : allUsers) {
            if ("dentist".equals(user.getRole())) {
                dentists.add(user);
            }
        }
    }
    
    private void loadUpcomingAppointments() {
        if (currentUser != null) {
            List<Appointment> appointments = rmiClient.getPatientAppointments(currentUser.getId());
            upcomingAppointments.clear();
            
            for (Appointment appointment : appointments) {
                if ("approved".equals(appointment.getStatus()) && 
                    appointment.getAppointmentTime().isAfter(LocalDateTime.now())) {
                    upcomingAppointments.add(appointment);
                }
            }
        }
    }
    
    private void loadPastAppointments() {
        if (currentUser != null) {
            List<Appointment> appointments = rmiClient.getPatientAppointments(currentUser.getId());
            pastAppointments.clear();
            
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentTime().isBefore(LocalDateTime.now()) ||
                    "cancelled".equals(appointment.getStatus())) {
                    pastAppointments.add(appointment);
                }
            }
        }
    }
    
    public boolean updateProfile(String firstName, String lastName, String email) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            errorMessage.set("All fields are required");
            return false;
        }
        
        if (currentUser != null) {
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            
            if (rmiClient.updateUser(currentUser)) {
                errorMessage.set("Profile updated successfully");
                this.firstName.set(firstName);
                this.lastName.set(lastName);
                this.userEmail.set(email);
                return true;
            } else {
                errorMessage.set("Failed to update profile");
                return false;
            }
        }
        return false;
    }
    
    public boolean cancelAppointment(Appointment appointment) {
        if (appointment == null) {
            errorMessage.set("Please select an appointment to cancel");
            return false;
        }
        
        // Check if appointment is within 24 hours
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        LocalDateTime now = LocalDateTime.now();
        
        if (appointmentTime.isBefore(now.plusHours(24))) {
            errorMessage.set("Appointments cannot be cancelled within 24 hours of the appointment time");
            return false;
        }
        
        if (rmiClient.updateAppointmentStatus(appointment.getId(), "cancelled")) {
            errorMessage.set("Appointment cancelled successfully");
            loadUpcomingAppointments();
            return true;
        } else {
            errorMessage.set("Failed to cancel appointment");
            return false;
        }
    }
    
    public void selectDentist(User dentist) {
        this.selectedDentist = dentist;
        loadDentistProfile();
    }
    
    public void selectDate(LocalDate date) {
        this.selectedDate = date;
        loadAvailableTimeSlots();
    }
    
    public void selectTimeSlot(String timeSlot) {
        this.selectedTimeSlot = timeSlot;
    }
    
    private void loadDentistProfile() {
        if (selectedDentist != null) {
            DentistProfile profile = rmiClient.getDentistProfile(selectedDentist.getId());
            StringBuilder profileText = new StringBuilder();
            
            if (profile != null) {
                if (profile.getSpecialization() != null && !profile.getSpecialization().trim().isEmpty()) {
                    profileText.append("Specialization: ").append(profile.getSpecialization()).append("\n\n");
                }
                
                if (profile.getDescription() != null && !profile.getDescription().trim().isEmpty()) {
                    profileText.append("Description:\n").append(profile.getDescription());
                } else {
                    profileText.append("No description available");
                }
            } else {
                profileText.append("No profile information available");
            }
            
            dentistProfile.set(profileText.toString());
        }
    }
    
    private void loadAvailableTimeSlots() {
        availableTimeSlots.clear();
        
        if (selectedDentist == null || selectedDate == null) {
            return;
        }
        
        List<WorkingHours> workingHours = rmiClient.getWorkingHours(selectedDentist.getId());
        String dayOfWeek = selectedDate.getDayOfWeek().toString();
        
        for (WorkingHours hours : workingHours) {
            if (dayOfWeek.equalsIgnoreCase(hours.getDayOfWeek())) {
                LocalTime start = hours.getStartTime();
                LocalTime end = hours.getEndTime();
                
                if (start != null && end != null) {
                    LocalTime current = start;
                    while (!current.isAfter(end.minusMinutes(30))) {
                        LocalDateTime slotDateTime = LocalDateTime.of(selectedDate, current);
                        
                        if (slotDateTime.isAfter(LocalDateTime.now())) {
                            List<Appointment> existingAppointments = rmiClient.getDentistAppointments(selectedDentist.getId());
                            boolean isSlotAvailable = true;
                            
                            for (Appointment appointment : existingAppointments) {
                                if (appointment.getAppointmentTime().equals(slotDateTime) && 
                                    "approved".equals(appointment.getStatus())) {
                                    isSlotAvailable = false;
                                    break;
                                }
                            }
                            
                            if (isSlotAvailable) {
                                availableTimeSlots.add(current.format(DateTimeFormatter.ofPattern("HH:mm")));
                            }
                        }
                        current = current.plusMinutes(30);
                    }
                }
                break;
            }
        }
    }
    
    public boolean bookAppointment() {
        if (selectedDentist == null || selectedDate == null || selectedTimeSlot == null) {
            errorMessage.set("Please select dentist, date and time slot");
            return false;
        }
        
        if (currentUser == null) {
            errorMessage.set("User not logged in");
            return false;
        }
        
        LocalTime time = LocalTime.parse(selectedTimeSlot, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime appointmentDateTime = LocalDateTime.of(selectedDate, time);
        
        // Check if the selected time is within dentist's working hours
        List<WorkingHours> workingHours = rmiClient.getWorkingHours(selectedDentist.getId());
        String dayOfWeek = selectedDate.getDayOfWeek().toString();
        boolean isWithinWorkingHours = false;
        
        for (WorkingHours hours : workingHours) {
            if (dayOfWeek.equalsIgnoreCase(hours.getDayOfWeek())) {
                LocalTime start = hours.getStartTime();
                LocalTime end = hours.getEndTime();
                
                if (start != null && end != null && !time.isBefore(start) && !time.isAfter(end)) {
                    isWithinWorkingHours = true;
                    break;
                }
            }
        }
        
        if (!isWithinWorkingHours) {
            errorMessage.set("Selected time is not within dentist's working hours");
            return false;
        }
        
        // Check if the time slot is still available
        List<Appointment> existingAppointments = rmiClient.getDentistAppointments(selectedDentist.getId());
        for (Appointment appointment : existingAppointments) {
            if (appointment.getAppointmentTime().equals(appointmentDateTime) && 
                ("approved".equals(appointment.getStatus()) || "pending".equals(appointment.getStatus()))) {
                errorMessage.set("This time slot is no longer available");
                return false;
            }
        }
        
        if (rmiClient.createAppointment(currentUser.getId(), selectedDentist.getId(), appointmentDateTime)) {
            errorMessage.set("Appointment booked successfully");
            loadUpcomingAppointments();
            clearBookingSelection();
            return true;
        } else {
            errorMessage.set("Failed to book appointment");
            return false;
        }
    }
    
    private void clearBookingSelection() {
        selectedDentist = null;
        selectedDate = null;
        selectedTimeSlot = null;
        availableTimeSlots.clear();
    }
    
    public String getFormattedDate(Appointment appointment) {
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        return appointmentTime != null ? appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
    
    public String getFormattedTime(Appointment appointment) {
        LocalDateTime appointmentTime = appointment.getAppointmentTime();
        return appointmentTime != null ? appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
    
    public String getDentistName(Appointment appointment) {
        int dentistId = appointment.getDentistId();
        for (User dentist : dentists) {
            if (dentist.getId() == dentistId) {
                return "Dr. " + dentist.getFirstName() + " " + dentist.getLastName();
            }
        }
        return "Unknown";
    }
    
    public String getFormattedStatus(Appointment appointment) {
        String status = appointment.getStatus();
        return status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown";
    }
    
    public String getDentistDisplayName(User dentist) {
        return dentist.getFirstName() + " " + dentist.getLastName();
    }
    
    public String getDentistSpecialization(User dentist) {
        DentistProfile profile = rmiClient.getDentistProfile(dentist.getId());
        if (profile != null && profile.getSpecialization() != null && !profile.getSpecialization().trim().isEmpty()) {
            return profile.getSpecialization();
        }
        return "Not specified";
    }
    
    public void clearError() {
        errorMessage.set("");
    }
} 