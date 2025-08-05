package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewHandler {
    private static ViewHandler instance;
    private Stage primaryStage;
    private Map<ViewType, Scene> scenes;
    private Map<ViewType, Object> controllers;
    private User currentUser;
    
    private ViewHandler(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.scenes = new HashMap<>();
        this.controllers = new HashMap<>();
    }
    
    public static ViewHandler getInstance() {
        return instance;
    }
    
    public static void initialize(Stage primaryStage) {
        instance = new ViewHandler(primaryStage);
    }
    
    public void openView(ViewType viewType) {
        openView(viewType, null);
    }
    
    public void openView(ViewType viewType, User user) {
        try {
            Scene scene = scenes.get(viewType);
            Object controller = controllers.get(viewType);
            
            if (scene == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(viewType.getFxmlPath()));
                Parent root = loader.load();
                
                double width = getSceneWidth(viewType);
                double height = getSceneHeight(viewType);
                
                scene = new Scene(root, width, height);
                scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
                scenes.put(viewType, scene);
                controller = loader.getController();
                controllers.put(viewType, controller);
            }
            
            if (user != null && controller != null) {
                setUserToController(controller, user);
            }
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(viewType.getTitle());
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private double getSceneWidth(ViewType viewType) {
        switch (viewType) {
            case LOGIN:
                return 500;
            case REGISTER:
                return 400;
            case PATIENT_DASHBOARD:
            case DENTIST_DASHBOARD:
            case SECRETARY_DASHBOARD:
                return 900;
            default:
                return 900;
        }
    }
    
    private double getSceneHeight(ViewType viewType) {
        switch (viewType) {
            case LOGIN:
                return 400;
            case REGISTER:
                return 400;
            case PATIENT_DASHBOARD:
            case DENTIST_DASHBOARD:
            case SECRETARY_DASHBOARD:
                return 600;
            default:
                return 600;
        }
    }
    
    private void setUserToController(Object controller, User user) {
        try {
            if (controller instanceof PatientDashboardController) {
                ((PatientDashboardController) controller).setUser(user);
            } else if (controller instanceof DentistDashboardController) {
                ((DentistDashboardController) controller).setUser(user);
            } else if (controller instanceof SecretaryDashboardController) {
                ((SecretaryDashboardController) controller).setUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void closeCurrentView() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
} 