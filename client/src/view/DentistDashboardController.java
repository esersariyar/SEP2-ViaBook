package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import model.BlockedSlot;
import service.RMIClient;
import java.net.URL;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DentistDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TextField specializationField;
    @FXML private TextArea descriptionArea;
    @FXML private Button updateProfileButton;
    
    @FXML private TableView<WorkingHours> workingHoursTable;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Button addWorkingHoursButton;
    @FXML private Button updateWorkingHoursButton;
    @FXML private Button deleteWorkingHoursButton;
    
    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    
    @FXML private DatePicker slotDatePicker;
    @FXML private ListView availableSlotsList;
    @FXML private Button blockSlotButton;
    @FXML private Button unblockSlotButton;
    
    private User currentUser;
    private RMIClient rmiClient = new RMIClient();
    private ObservableList<WorkingHours> workingHoursList = FXCollections.observableArrayList();
    private ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
    private ObservableList<String> availableSlots = FXCollections.observableArrayList();
    private BlockedSlot selectedBlockedSlot;
    private java.util.Map<String, Integer> blockedSlotMap = new java.util.HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dayComboBox != null) {
            dayComboBox.getItems().addAll(
                "Monday", "Tuesday", "Wednesday", "Thursday", 
                "Friday", "Saturday", "Sunday"
            );
        }
        
        setupWorkingHoursTable();
        setupUpcomingAppointmentsTable();
        setupSlotManagement();
    }
    
    private void setupSlotManagement() {
        if (availableSlotsList != null) {
            availableSlotsList.setItems(availableSlots);
        }
    }
    
    private void setupUpcomingAppointmentsTable() {
        if (upcomingAppointmentsTable != null && upcomingAppointmentsTable.getColumns().size() >= 4) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> patientColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(3);
            
            dateColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(
                    cellData.getValue().getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            });
            
            timeColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(
                    cellData.getValue().getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            });
            
            patientColumn.setCellValueFactory(cellData -> {
                User patient = getUserById(cellData.getValue().getPatientId());
                return new SimpleStringProperty(
                    patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown"
                );
            });
            
            statusColumn.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatus();
                return new SimpleStringProperty(
                    status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown"
                );
            });
            
            upcomingAppointmentsTable.setItems(upcomingAppointments);
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
    
    private void setupWorkingHoursTable() {
        if (workingHoursTable != null && workingHoursTable.getColumns().size() >= 3) {
            TableColumn<WorkingHours, String> dayColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(0);
            TableColumn<WorkingHours, String> startColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(1);
            TableColumn<WorkingHours, String> endColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(2);
            
            dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
            startColumn.setCellValueFactory(cellData -> {
                LocalTime startTime = cellData.getValue().getStartTime();
                return new SimpleStringProperty(
                    startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : ""
                );
            });
            endColumn.setCellValueFactory(cellData -> {
                LocalTime endTime = cellData.getValue().getEndTime();
                return new SimpleStringProperty(
                    endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : ""
                );
            });
            
            workingHoursTable.setItems(workingHoursList);
        }
    }

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
            currentUser = user;
            
            loadDentistProfile();
            loadWorkingHours();
            loadUpcomingAppointments();
        }
    }
    
    private void loadDentistProfile() {
        if (currentUser != null) {
            DentistProfile profile = rmiClient.getDentistProfile(currentUser.getId());
            if (profile != null) {
                if (specializationField != null) {
                    specializationField.setText(profile.getSpecialization() != null ? profile.getSpecialization() : "");
                }
                if (descriptionArea != null) {
                    descriptionArea.setText(profile.getDescription() != null ? profile.getDescription() : "");
                }
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
            
            // Filter only upcoming appointments (future appointments)
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentTime().isAfter(now) && !"cancelled".equals(appointment.getStatus())) {
                    upcomingAppointments.add(appointment);
                }
            }
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
        
        String specialization = specializationField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        DentistProfile profile = new DentistProfile(currentUser.getId(), description, specialization);
        
        if (rmiClient.updateDentistProfile(profile)) {
            showAlert("Success", "Profile updated successfully");
        } else {
            showAlert("Error", "Failed to update profile");
        }
    }
    
    @FXML
    private void handleAddWorkingHours() {
        if (currentUser == null) {
            showAlert("Error", "No user logged in");
            return;
        }
        
        String selectedDay = dayComboBox.getSelectionModel().getSelectedItem();
        String startTimeText = startTimeField.getText().trim();
        String endTimeText = endTimeField.getText().trim();
        
        if (selectedDay == null || startTimeText.isEmpty() || endTimeText.isEmpty()) {
            showAlert("Error", "Please fill all fields");
            return;
        }
        
        try {
            LocalTime startTime = LocalTime.parse(startTimeText, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeText, DateTimeFormatter.ofPattern("HH:mm"));
            
            if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                showAlert("Error", "Start time must be before end time");
                return;
            }
            
            WorkingHours workingHours = new WorkingHours(currentUser.getId(), selectedDay, startTime, endTime);
            
            if (rmiClient.addWorkingHours(workingHours)) {
                showAlert("Success", "Working hours added successfully");
                loadWorkingHours();
                clearWorkingHoursFields();
            } else {
                showAlert("Error", "Failed to add working hours. Day might already exist.");
            }
        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid time format. Use HH:mm (e.g., 09:00)");
        }
    }
    
    @FXML
    private void handleUpdateWorkingHours() {
        WorkingHours selectedHours = workingHoursTable.getSelectionModel().getSelectedItem();
        if (selectedHours == null) {
            showAlert("Error", "Please select working hours to update");
            return;
        }
        
        String startTimeText = startTimeField.getText().trim();
        String endTimeText = endTimeField.getText().trim();
        
        if (startTimeText.isEmpty() || endTimeText.isEmpty()) {
            showAlert("Error", "Please fill start and end time fields");
            return;
        }
        
        try {
            LocalTime startTime = LocalTime.parse(startTimeText, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeText, DateTimeFormatter.ofPattern("HH:mm"));
            
            if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                showAlert("Error", "Start time must be before end time");
                return;
            }
            
            WorkingHours updatedHours = new WorkingHours(currentUser.getId(), selectedHours.getDayOfWeek(), startTime, endTime);
            
            if (rmiClient.updateWorkingHours(updatedHours)) {
                showAlert("Success", "Working hours updated successfully");
                loadWorkingHours();
                clearWorkingHoursFields();
            } else {
                showAlert("Error", "Failed to update working hours");
            }
        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid time format. Use HH:mm (e.g., 09:00)");
        }
    }
    
    @FXML
    private void handleDeleteWorkingHours() {
        WorkingHours selectedHours = workingHoursTable.getSelectionModel().getSelectedItem();
        if (selectedHours == null) {
            showAlert("Error", "Please select working hours to delete");
            return;
        }
        
        if (rmiClient.deleteWorkingHours(currentUser.getId(), selectedHours.getDayOfWeek())) {
            showAlert("Success", "Working hours deleted successfully");
            loadWorkingHours();
            clearWorkingHoursFields();
        } else {
            showAlert("Error", "Failed to delete working hours");
        }
    }
    
    @FXML
    private void handleWorkingHoursSelection() {
        WorkingHours selectedHours = workingHoursTable.getSelectionModel().getSelectedItem();
        if (selectedHours != null) {
            dayComboBox.getSelectionModel().select(selectedHours.getDayOfWeek());
            startTimeField.setText(selectedHours.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            endTimeField.setText(selectedHours.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }
    
    private void clearWorkingHoursFields() {
        dayComboBox.getSelectionModel().clearSelection();
        startTimeField.clear();
        endTimeField.clear();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleSlotDateSelection() {
        LocalDate selectedDate = slotDatePicker.getValue();
        if (selectedDate != null && currentUser != null) {
            loadAvailableSlotsForDate(selectedDate);
        }
    }
    
    private void loadAvailableSlotsForDate(LocalDate date) {
        availableSlots.clear();
        blockedSlotMap.clear();
        if (date.isBefore(LocalDate.now())) {
            availableSlots.add("Cannot manage slots for past dates");
            return;
        }
        String dayOfWeek = date.getDayOfWeek().toString();
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
        List<WorkingHours> workingHoursList = rmiClient.getWorkingHours(currentUser.getId());
        WorkingHours workingHoursForDay = null;
        for (WorkingHours wh : workingHoursList) {
            if (wh.getDayOfWeek().equals(dayOfWeek)) {
                workingHoursForDay = wh;
                break;
            }
        }
        if (workingHoursForDay == null) {
            availableSlots.add("No working hours set for " + dayOfWeek);
            return;
        }
        // Get blocked slots for the day
        List<BlockedSlot> blockedSlots = rmiClient.getBlockedSlots(currentUser.getId());
        java.util.Set<String> blockedTimes = new java.util.HashSet<>();
        for (BlockedSlot bs : blockedSlots) {
            if (bs.getBlockedTime().toLocalDate().equals(date)) {
                String timeStr = bs.getBlockedTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                blockedTimes.add(timeStr);
                blockedSlotMap.put(timeStr, bs.getId());
            }
        }
        LocalTime startTime = workingHoursForDay.getStartTime();
        LocalTime endTime = workingHoursForDay.getEndTime();
        List<String> timeSlots = new ArrayList<>();
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            String timeStr = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, currentTime);
            if (blockedTimes.contains(timeStr)) {
                timeSlots.add(timeStr + " - Blocked");
            } else if (rmiClient.isTimeSlotAvailable(currentUser.getId(), appointmentDateTime)) {
                timeSlots.add(timeStr + " - Available");
            } else {
                timeSlots.add(timeStr + " - Booked");
            }
            currentTime = currentTime.plusMinutes(30);
        }
        if (timeSlots.isEmpty()) {
            availableSlots.add("No time slots available");
        } else {
            availableSlots.addAll(timeSlots);
        }
    }
    
    @FXML
    private void handleSlotSelection(MouseEvent event) {
        Object selectedItem = availableSlotsList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String selectedSlot = selectedItem.toString();
            if (selectedSlot.contains("Available")) {
                System.out.println("Selected available slot: " + selectedSlot);
            }
        }
    }
    
    @FXML
    private void handleBlockSlot() {
        Object selectedItem = availableSlotsList.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = slotDatePicker.getValue();
        
        if (selectedItem == null) {
            showAlert("Error", "Please select a slot to block");
            return;
        }
        
        String selectedSlot = selectedItem.toString();
        if (!selectedSlot.contains("Available")) {
            showAlert("Error", "Please select an available slot to block");
            return;
        }
        
        if (selectedDate == null) {
            showAlert("Error", "Please select a date");
            return;
        }
        
        // Parse selected time
        String timeStr = selectedSlot.split(" - ")[0];
        LocalTime selectedTime;
        try {
            selectedTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            showAlert("Error", "Invalid time format");
            return;
        }
        
        LocalDateTime blockedDateTime = LocalDateTime.of(selectedDate, selectedTime);
        BlockedSlot blockedSlot = new BlockedSlot(currentUser.getId(), blockedDateTime, "Blocked by dentist");
        
        if (rmiClient.createBlockedSlot(blockedSlot)) {
            showAlert("Success", "Slot blocked successfully");
            loadAvailableSlotsForDate(selectedDate);
        } else {
            showAlert("Error", "Failed to block slot");
        }
    }

    @FXML
    private void handleUnblockSlot() {
        Object selectedItem = availableSlotsList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String selectedSlot = selectedItem.toString();
            if (selectedSlot.contains("Blocked")) {
                String timeStr = selectedSlot.split(" - ")[0];
                Integer blockedSlotId = blockedSlotMap.get(timeStr);
                if (blockedSlotId != null) {
                    if (rmiClient.deleteBlockedSlot(blockedSlotId)) {
                        showAlert("Success", "Slot unblocked successfully");
                        LocalDate selectedDate = slotDatePicker.getValue();
                        if (selectedDate != null) {
                            loadAvailableSlotsForDate(selectedDate);
                        }
                    } else {
                        showAlert("Error", "Failed to unblock slot");
                    }
                }
            } else {
                showAlert("Info", "Please select a blocked slot to unblock.");
            }
        } else {
            showAlert("Info", "Please select a blocked slot to unblock.");
        }
    }
} 