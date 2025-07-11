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
import service.RMIClient;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PatientDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button updateProfileButton;
    
    @FXML private TableView upcomingAppointmentsTable;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDentistTable();
        loadDentists();
        
        if (timeSlotsList != null) {
            timeSlotsList.setItems(availableTimeSlots);
        }
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

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
            
            if (firstNameField != null) firstNameField.setText(user.getFirstName());
            if (lastNameField != null) lastNameField.setText(user.getLastName());
            if (emailField != null) emailField.setText(user.getEmail());
            currentUser = user;
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
        System.out.println("Cancel appointment clicked");
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
        
        if (selectedDate.isBefore(LocalDate.now())) {
            availableTimeSlots.add("Cannot book appointments for past dates");
            return;
        }
        
        // Get dentists working hours for the selected day
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
            timeSlots.add(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
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
        
        showAlert("Info", "apointment success WIP TODO!!! \n\n" +
                "Selected:\n" +
                "Dentist: Dr. " + selectedDentist.getFirstName() + " " + selectedDentist.getLastName() + "\n" +
                "Date: " + selectedDate + "\n" +
                "Time: " + selectedTimeSlot);
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