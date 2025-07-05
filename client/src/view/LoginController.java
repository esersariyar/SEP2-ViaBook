import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    private LoginViewModel viewModel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new LoginViewModel();
        setupBindings();
        setupEventHandlers();
    }
    
    private void setupBindings() {
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
    }
    
    private void setupEventHandlers() {
        passwordField.setOnAction(e -> handleLogin());
    }
    
    @FXML
    private void handleLogin() {
        boolean success = viewModel.handleLogin();
        
        if (success) {
            showSuccessDialog("Welcome to ViaBook, " + viewModel.usernameProperty().get() + "!");
            viewModel.clearFields();
        }
    }
    
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ViaBook - Login Successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 