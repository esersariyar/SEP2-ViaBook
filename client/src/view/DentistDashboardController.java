package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DentistDashboardController {
    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        System.out.println("Logout clicked");
    }
} 