package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PatientDashboardController extends BaseDashboardController {
    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
} 