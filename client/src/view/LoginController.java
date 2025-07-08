import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    private LoginViewModel viewModel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new LoginViewModel();
        setupBindings();
        setupEventHandlers();
        errorLabel.setVisible(true);
    }
    
    private void setupBindings() {
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
    }
    
    private void setupEventHandlers() {
        passwordField.setOnAction(e -> handleLogin());
    }
    
    @FXML
    private void handleLogin() {
        System.out.println("ErrorLabel: " + errorLabel.getText());
        System.out.println("ErrorMessage: " + viewModel.errorMessageProperty().get());
        boolean success = viewModel.handleLogin();
        
        if (success) {
            User user = viewModel.getAuthenticatedUser();
            viewModel.clearFields();
            if (user != null) {
                if ("patient".equalsIgnoreCase(user.getRole())) {
                    openDashboard("patient_dashboard.fxml", "Patient Dashboard");
                } else if ("dentist".equalsIgnoreCase(user.getRole())) {
                    openDashboard("dentist_dashboard.fxml", "Dentist Dashboard");
                } else if ("secretary".equalsIgnoreCase(user.getRole())) {
                    openDashboard("secretary_dashboard.fxml", "Secretary Dashboard");
                } else {
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setTitle("Error");
                   alert.setHeaderText(null);
                   alert.setContentText("Invalid user role");
                   alert.showAndWait();
                }
            }
        }
    }
    
    private void openDashboard(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 