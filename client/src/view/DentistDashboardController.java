package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DentistDashboardController extends BaseDashboardController {
    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
} 