package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import service.RMIClient;
import java.net.URL;
import java.util.ResourceBundle;

public class DentistDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TextField specializationField;
    @FXML private TextArea descriptionArea;
    @FXML private Button updateProfileButton;
    
    @FXML private TableView workingHoursTable;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dayComboBox != null) {
            dayComboBox.getItems().addAll(
                "Monday", "Tuesday", "Wednesday", "Thursday", 
                "Friday", "Saturday", "Sunday"
            );
        }
    }

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
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
        
        String specialization = specializationField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        showAlert("Info", "Profile updated (specialization and description features coming soon)");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleAddWorkingHours() {
        // TODO: Implement add working hours functionality
        System.out.println("Add working hours clicked");
    }
    
    @FXML
    private void handleUpdateWorkingHours() {
        // TODO: Implement update working hours functionality
        System.out.println("Update working hours clicked");
    }
    
    @FXML
    private void handleDeleteWorkingHours() {
        // TODO: Implement delete working hours functionality
        System.out.println("Delete working hours clicked");
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