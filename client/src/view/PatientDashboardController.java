package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.User;
import service.RMIClient;

public class PatientDashboardController extends BaseDashboardController {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    // Profile editing components
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button updateProfileButton;
    
    // Appointments components
    @FXML private TableView upcomingAppointmentsTable;
    @FXML private TableView pastAppointmentsTable;
    @FXML private Button cancelAppointmentButton;
    
    // Booking components
    @FXML private ComboBox dentistComboBox;
    @FXML private TextArea dentistProfileArea;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ListView timeSlotsList;
    @FXML private Button bookAppointmentButton;
    
    // Browse dentists components
    @FXML private TableView dentistsTable;
    @FXML private TextArea selectedDentistDescArea;

    private User currentUser;
    private RMIClient rmiClient = new RMIClient();

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
            
            // Set profile editing fields
            if (firstNameField != null) firstNameField.setText(user.getFirstName());
            if (lastNameField != null) lastNameField.setText(user.getLastName());
            if (emailField != null) emailField.setText(user.getEmail());
            currentUser = user; // Assign user to currentUser
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
        // TODO: Implement appointment cancellation
        System.out.println("Cancel appointment clicked");
    }
    
    @FXML
    private void handleDentistSelection() {
        // TODO: Implement dentist selection logic
        System.out.println("Dentist selected");
    }
    
    @FXML
    private void handleDateSelection() {
        // TODO: Implement date selection logic
        System.out.println("Date selected");
    }
    
    @FXML
    private void handleTimeSlotSelection(MouseEvent event) {
        // TODO: Implement time slot selection
        System.out.println("Time slot selected");
    }
    
    @FXML
    private void handleBookAppointment() {
        // TODO: Implement appointment booking
        System.out.println("Book appointment clicked");
    }
    
    @FXML
    private void handleDentistTableSelection(MouseEvent event) {
        // TODO: Implement dentist table selection
        System.out.println("Dentist selected from table");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 