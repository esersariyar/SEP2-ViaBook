package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public abstract class BaseDashboardController {
    protected void logout(Button logoutButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("ViaBook Login");
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.show();
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 