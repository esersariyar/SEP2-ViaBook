package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import service.RMIClient;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    
    @FXML private TableView upcomingAppointmentsTable;
    
    @FXML private DatePicker slotDatePicker;
    @FXML private ListView availableSlotsList;
    @FXML private Button blockSlotButton;
    @FXML private Button unblockSlotButton;
    @FXML private TextField customBlockTimeField;
    @FXML private Button customBlockButton;
    
    private User currentUser;
    private RMIClient rmiClient = new RMIClient();
    private ObservableList<WorkingHours> workingHoursList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dayComboBox != null) {
            dayComboBox.getItems().addAll(
                "Monday", "Tuesday", "Wednesday", "Thursday", 
                "Friday", "Saturday", "Sunday"
            );
        }
        
        setupWorkingHoursTable();
    }
    
    private void setupWorkingHoursTable() {
        if (workingHoursTable != null && workingHoursTable.getColumns().size() >= 3) {
            TableColumn<WorkingHours, String> dayColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(0);
            TableColumn<WorkingHours, String> startColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(1);
            TableColumn<WorkingHours, String> endColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(2);
            
            dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
            startColumn.setCellValueFactory(cellData -> {
                LocalTime startTime = cellData.getValue().getStartTime();
                return new javafx.beans.property.SimpleStringProperty(
                    startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : ""
                );
            });
            endColumn.setCellValueFactory(cellData -> {
                LocalTime endTime = cellData.getValue().getEndTime();
                return new javafx.beans.property.SimpleStringProperty(
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
        // TODO: Implement slot date selection logic
        System.out.println("Slot date selected");
    }
    
    @FXML
    private void handleSlotSelection(MouseEvent event) {
        // TODO: Implement slot selection logic
        System.out.println("Slot selected");
    }
    
    @FXML
    private void handleBlockSlot() {
        // TODO: Implement block slot functionality
        System.out.println("Block slot clicked");
    }
    
    @FXML
    private void handleUnblockSlot() {
        // TODO: Implement unblock slot functionality
        System.out.println("Unblock slot clicked");
    }
    
    @FXML
    private void handleCustomBlock() {
        // TODO: Implement custom block functionality
        System.out.println("Custom block clicked");
    }
} 