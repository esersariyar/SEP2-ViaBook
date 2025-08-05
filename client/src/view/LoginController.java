package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import viewmodel.LoginViewModel;
import view.ViewHandler;
import view.ViewType;

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
                    ViewHandler.getInstance().openView(ViewType.PATIENT_DASHBOARD, user);
                } else if ("dentist".equalsIgnoreCase(user.getRole())) {
                    ViewHandler.getInstance().openView(ViewType.DENTIST_DASHBOARD, user);
                } else if ("secretary".equalsIgnoreCase(user.getRole())) {
                    ViewHandler.getInstance().openView(ViewType.SECRETARY_DASHBOARD, user);
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

    @FXML
    private void handleRegister() {
        ViewHandler.getInstance().openView(ViewType.REGISTER);
    }
} 