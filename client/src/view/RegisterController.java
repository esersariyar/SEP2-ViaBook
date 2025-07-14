package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import service.RMIClient;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField pinField;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;

    private RMIClient rmiClient = new RMIClient();

    @FXML
    public void initialize() {
        ObservableList<String> roles = FXCollections.observableArrayList("patient", "dentist", "secretary");
        roleComboBox.setItems(roles);
        roleComboBox.setValue("patient");
        pinField.setVisible(false);
        pinField.setManaged(false);
        roleComboBox.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            boolean showPin = "dentist".equals(selectedRole) || "secretary".equals(selectedRole);
            pinField.setVisible(showPin);
            pinField.setManaged(showPin);
        });
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        String pin = pinField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (("dentist".equals(role) || "secretary".equals(role))) {
            if (pin == null || !pin.equals("0000")) {
                errorLabel.setText("PIN code is required for this role.");
                return;
            }
        }
        boolean success = rmiClient.registerUser(password, email, firstName, lastName, role);
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Registration successful! You can now log in.");
            alert.showAndWait();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Registration failed. Email may already be in use.");
        }
    }
} 