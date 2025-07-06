package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PatientDashboardController {
    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        System.out.println("Logout clicked");
    }
} 