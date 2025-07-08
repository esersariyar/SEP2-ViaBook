package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.User;

public class PatientDashboardController extends BaseDashboardController {
    @FXML
    private Button logoutButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label emailLabel;

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
        }
    }

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
} 