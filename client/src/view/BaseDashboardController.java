package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public abstract class BaseDashboardController {
    protected void logout(Button logoutButton) {
        ViewHandler.getInstance().openView(ViewType.LOGIN);
    }
} 