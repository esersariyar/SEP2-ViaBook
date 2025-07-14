package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import service.RMIClient;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class PatientDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button updateProfileButton;
    
    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    @FXML private TableView pastAppointmentsTable;
    @FXML private Button cancelAppointmentButton;
    
    @FXML private ComboBox<User> dentistComboBox;
    @FXML private TextArea dentistProfileArea;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ListView<String> timeSlotsList;
    @FXML private Button bookAppointmentButton;
    
    @FXML private TableView<User> dentistsTable;
    @FXML private TextArea selectedDentistDescArea;
    
    private User currentUser;
    private RMIClient rmiClient = new RMIClient();
    private ObservableList<String> availableTimeSlots = FXCollections.observableArrayList();
    private ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> pastAppointments = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDentistTable();
        setupUpcomingAppointmentsTable();
        setupPastAppointmentsTable();
        loadDentists();
        if (timeSlotsList != null) {
            timeSlotsList.setItems(availableTimeSlots);
        }
    }
    
    private void setupUpcomingAppointmentsTable() {
        if (upcomingAppointmentsTable != null && upcomingAppointmentsTable.getColumns().size() >= 5) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(3);
            TableColumn<Appointment, String> actionsColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(4);
            
            dateColumn.setCellValueFactory(cellData -> {
                LocalDateTime appointmentTime = cellData.getValue().getAppointmentTime();
                return new SimpleStringProperty(
                    appointmentTime != null ? appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
                );
            });
            
            timeColumn.setCellValueFactory(cellData -> {
                LocalDateTime appointmentTime = cellData.getValue().getAppointmentTime();
                return new SimpleStringProperty(
                    appointmentTime != null ? appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm")) : ""
                );
            });
            
            dentistColumn.setCellValueFactory(cellData -> {
                int dentistId = cellData.getValue().getDentistId();
                User dentist = getUserById(dentistId);
                return new SimpleStringProperty(
                    dentist != null ? "Dr. " + dentist.getFirstName() + " " + dentist.getLastName() : "Unknown"
                );
            });
            
            statusColumn.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatus();
                return new SimpleStringProperty(
                    status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown"
                );
            });
            
            actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Cancel"));
            
            upcomingAppointmentsTable.setItems(upcomingAppointments);
        }
    }
    
    private void setupPastAppointmentsTable() {
        if (pastAppointmentsTable != null && pastAppointmentsTable.getColumns().size() >= 4) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(3);

            dateColumn.setCellValueFactory(cellData -> {
                LocalDateTime appointmentTime = cellData.getValue().getAppointmentTime();
                return new SimpleStringProperty(
                    appointmentTime != null ? appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
                );
            });

            timeColumn.setCellValueFactory(cellData -> {
                LocalDateTime appointmentTime = cellData.getValue().getAppointmentTime();
                return new SimpleStringProperty(
                    appointmentTime != null ? appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm")) : ""
                );
            });

            dentistColumn.setCellValueFactory(cellData -> {
                int dentistId = cellData.getValue().getDentistId();
                User dentist = getUserById(dentistId);
                return new SimpleStringProperty(
                    dentist != null ? "Dr. " + dentist.getFirstName() + " " + dentist.getLastName() : "Unknown"
                );
            });

            statusColumn.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatus();
                return new SimpleStringProperty(
                    status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown"
                );
            });

            pastAppointmentsTable.setItems(pastAppointments);
        }
    }
    
    private User getUserById(int userId) {
        List<User> allUsers = rmiClient.getAllUsers();
        for (User user : allUsers) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }
    
    private void setupDentistTable() {
        if (dentistsTable != null && dentistsTable.getColumns().size() >= 2) {
            TableColumn<User, String> nameColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(0);
            TableColumn<User, String> specializationColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(1);
            
            nameColumn.setCellValueFactory(cellData -> {
                User user = cellData.getValue();
                return new SimpleStringProperty(user.getFirstName() + " " + user.getLastName());
            });
            
            specializationColumn.setCellValueFactory(cellData -> {
                User user = cellData.getValue();
                DentistProfile profile = rmiClient.getDentistProfile(user.getId());
                if (profile != null && profile.getSpecialization() != null && !profile.getSpecialization().trim().isEmpty()) {
                    return new SimpleStringProperty(profile.getSpecialization());
                }
                return new SimpleStringProperty("Not specified");
            });
        }
    }
    
    private void loadDentists() {
        List<User> allUsers = rmiClient.getAllUsers();
        ObservableList<User> dentists = FXCollections.observableArrayList();
        
        for (User user : allUsers) {
            if ("dentist".equals(user.getRole())) {
                dentists.add(user);
            }
        }
        
        if (dentistComboBox != null) {
            dentistComboBox.setItems(dentists);
            dentistComboBox.setCellFactory(listView -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText("Dr. " + user.getFirstName() + " " + user.getLastName());
                    }
                }
            });
            dentistComboBox.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText("Select Dentist");
                    } else {
                        setText("Dr. " + user.getFirstName() + " " + user.getLastName());
                    }
                }
            });
        }
        
        if (dentistsTable != null) {
            dentistsTable.setItems(dentists);
        }
    }
    
    private void loadUpcomingAppointments() {
        if (currentUser != null) {
            List<Appointment> appointments = rmiClient.getPatientAppointments(currentUser.getId());
            upcomingAppointments.clear();
            
            // Filter only upcoming appointments (future appointments)
            LocalDateTime now = LocalDateTime.now();
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentTime().isAfter(now) && !"cancelled".equals(appointment.getStatus())) {
                    upcomingAppointments.add(appointment);
                }
            }
        }
    }

    private void loadPastAppointments() {
        if (currentUser != null) {
            List<Appointment> appointments = rmiClient.getPastAppointmentsByPatientId(currentUser.getId());
            pastAppointments.clear();
            pastAppointments.addAll(appointments);
        }
    }

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
            
            if (firstNameField != null) firstNameField.setText(user.getFirstName());
            if (lastNameField != null) lastNameField.setText(user.getLastName());
            if (emailField != null) emailField.setText(user.getEmail());
            currentUser = user;
            
            // Load user's appointments
            loadUpcomingAppointments();
            loadPastAppointments();
        }
    }

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
    
    @FXML
    private void handleUpdateProfile() {
        if (currentUser == null) {
            showAlert("Error", "No user logged in");
            return;
        }
        
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert("Error", "All fields are required");
            return;
        }
        
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        
        if (rmiClient.updateUser(currentUser)) {
            nameLabel.setText(firstName);
            surnameLabel.setText(lastName);
            emailLabel.setText(email);
            showAlert("Success", "Profile updated successfully");
        } else {
            showAlert("Error", "Failed to update profile");
        }
    }
    
    @FXML
    private void handleCancelAppointment() {
        Appointment selectedAppointment = upcomingAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("Error", "Please select an appointment to cancel");
            return;
        }
        
        // Check if appointment is within 24 hours
        LocalDateTime appointmentTime = selectedAppointment.getAppointmentTime();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = appointmentTime.minusHours(24);
        
        if (now.isAfter(cancelDeadline)) {
            showAlert("Error", "Appointments can only be cancelled at least 24 hours before the scheduled time.\n\n" +
                    "Appointment time: " + appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                    "Cancel deadline: " + cancelDeadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Appointment");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to cancel this appointment?\n\n" +
                "Date: " + appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Time: " + appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (rmiClient.updateAppointmentStatus(selectedAppointment.getId(), "cancelled")) {
                showAlert("Success", "Appointment cancelled successfully");
                loadUpcomingAppointments(); // Refresh the table
            } else {
                showAlert("Error", "Failed to cancel appointment");
            }
        }
    }
    
    @FXML
    private void handleDentistSelection() {
        User selectedDentist = dentistComboBox.getSelectionModel().getSelectedItem();
        if (selectedDentist != null) {
            DentistProfile profile = rmiClient.getDentistProfile(selectedDentist.getId());
            String profileText = "Dr. " + selectedDentist.getFirstName() + " " + selectedDentist.getLastName() + "\n";
            
            if (profile != null) {
                profileText += "Specialization: " + (profile.getSpecialization() != null && !profile.getSpecialization().trim().isEmpty() ? profile.getSpecialization() : "Not specified") + "\n";
                profileText += "Email: " + selectedDentist.getEmail() + "\n";
                profileText += "Description: " + (profile.getDescription() != null && !profile.getDescription().trim().isEmpty() ? profile.getDescription() : "No description provided");
            } else {
                profileText += "Specialization: Not specified\n";
                profileText += "Email: " + selectedDentist.getEmail() + "\n";
                profileText += "Profile information not available";
            }
            
            dentistProfileArea.setText(profileText);
            
            // Load available times when dentist is selected
            loadAvailableTimeSlots();
        }
    }
    
    @FXML
    private void handleDateSelection() {
        loadAvailableTimeSlots();
    }
    
    private void loadAvailableTimeSlots() {
        availableTimeSlots.clear();
        
        User selectedDentist = dentistComboBox.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = appointmentDatePicker.getValue();
        
        if (selectedDentist == null || selectedDate == null) {
            return;
        }
        
        // Don't allow booking for past dates
        if (selectedDate.isBefore(LocalDate.now())) {
            availableTimeSlots.add("Cannot book appointments for past dates");
            return;
        }
        
        // Get dentist's working hours for the selected day
        String dayOfWeek = selectedDate.getDayOfWeek().toString();
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
        
        List<WorkingHours> workingHoursList = rmiClient.getWorkingHours(selectedDentist.getId());
        WorkingHours workingHoursForDay = null;
        
        for (WorkingHours wh : workingHoursList) {
            if (wh.getDayOfWeek().equals(dayOfWeek)) {
                workingHoursForDay = wh;
                break;
            }
        }
        
        if (workingHoursForDay == null) {
            availableTimeSlots.add("No working hours set for " + dayOfWeek);
            return;
        }
        
        // Generate time slots (every 30 minutes)
        LocalTime startTime = workingHoursForDay.getStartTime();
        LocalTime endTime = workingHoursForDay.getEndTime();
        
        List<String> timeSlots = new ArrayList<>();
        LocalTime currentTime = startTime;
        
        while (currentTime.isBefore(endTime)) {
            // Check if time slot is available
            LocalDateTime appointmentDateTime = LocalDateTime.of(selectedDate, currentTime);
            if (rmiClient.isTimeSlotAvailable(selectedDentist.getId(), appointmentDateTime)) {
                timeSlots.add(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            currentTime = currentTime.plusMinutes(30);
        }
        
        if (timeSlots.isEmpty()) {
            availableTimeSlots.add("No available time slots");
        } else {
            availableTimeSlots.addAll(timeSlots);
        }
    }
    
    @FXML
    private void handleTimeSlotSelection(MouseEvent event) {
        String selectedTimeSlot = timeSlotsList.getSelectionModel().getSelectedItem();
        if (selectedTimeSlot != null && !selectedTimeSlot.contains("No") && !selectedTimeSlot.contains("Cannot")) {
            System.out.println("Selected time slot: " + selectedTimeSlot);
        }
    }
    
    @FXML
    private void handleBookAppointment() {
        if (currentUser == null) {
            showAlert("Error", "No user logged in");
            return;
        }
        
        User selectedDentist = dentistComboBox.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = appointmentDatePicker.getValue();
        String selectedTimeSlot = timeSlotsList.getSelectionModel().getSelectedItem();
        
        if (selectedDentist == null) {
            showAlert("Error", "Please select a dentist");
            return;
        }
        
        if (selectedDate == null) {
            showAlert("Error", "Please select a date");
            return;
        }
        
        if (selectedTimeSlot == null || selectedTimeSlot.contains("No") || selectedTimeSlot.contains("Cannot")) {
            showAlert("Error", "Please select a valid time slot");
            return;
        }
        
        // Parse selected time
        LocalTime selectedTime;
        try {
            selectedTime = LocalTime.parse(selectedTimeSlot, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            showAlert("Error", "Invalid time format");
            return;
        }
        
        LocalDateTime appointmentDateTime = LocalDateTime.of(selectedDate, selectedTime);
        
        // Create appointment
        if (rmiClient.createAppointment(currentUser.getId(), selectedDentist.getId(), appointmentDateTime)) {
            showAlert("Success", "Appointment booked successfully!\n\n" +
                    "Details:\n" +
                    "Dentist: Dr. " + selectedDentist.getFirstName() + " " + selectedDentist.getLastName() + "\n" +
                    "Date: " + selectedDate + "\n" +
                    "Time: " + selectedTimeSlot + "\n" +
                    "Status: Pending");
            
            // Clear selections
            dentistComboBox.getSelectionModel().clearSelection();
            appointmentDatePicker.setValue(null);
            timeSlotsList.getSelectionModel().clearSelection();
            dentistProfileArea.clear();
            availableTimeSlots.clear();
            
            // Refresh appointments table
            loadUpcomingAppointments();
        } else {
            showAlert("Error", "Failed to book appointment. Time slot might be already taken.");
        }
    }
    
    @FXML
    private void handleDentistTableSelection(MouseEvent event) {
        User selectedDentist = dentistsTable.getSelectionModel().getSelectedItem();
        if (selectedDentist != null) {
            DentistProfile profile = rmiClient.getDentistProfile(selectedDentist.getId());
            String profileText = "Dr. " + selectedDentist.getFirstName() + " " + selectedDentist.getLastName() + "\n";
            
            if (profile != null) {
                profileText += "Specialization: " + (profile.getSpecialization() != null && !profile.getSpecialization().trim().isEmpty() ? profile.getSpecialization() : "Not specified") + "\n";
                profileText += "Email: " + selectedDentist.getEmail() + "\n";
                profileText += "Description: " + (profile.getDescription() != null && !profile.getDescription().trim().isEmpty() ? profile.getDescription() : "No description provided");
            } else {
                profileText += "Specialization: Not specified\n";
                profileText += "Email: " + selectedDentist.getEmail() + "\n";
                profileText += "Profile information not available";
            }
            
            selectedDentistDescArea.setText(profileText);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 