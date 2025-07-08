package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecretaryDashboardController extends BaseDashboardController {
    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
} 