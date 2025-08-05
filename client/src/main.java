import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.ViewHandler;
import view.ViewType;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewHandler.initialize(primaryStage);
        ViewHandler.getInstance().openView(ViewType.LOGIN);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 