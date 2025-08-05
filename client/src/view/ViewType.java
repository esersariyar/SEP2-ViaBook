package view;

public enum ViewType {
    LOGIN("login.fxml", "ViaBook Login"),
    REGISTER("register.fxml", "Register"),
    PATIENT_DASHBOARD("patient_dashboard.fxml", "Patient Dashboard"),
    DENTIST_DASHBOARD("dentist_dashboard.fxml", "Dentist Dashboard"),
    SECRETARY_DASHBOARD("secretary_dashboard.fxml", "Secretary Dashboard");
    
    private final String fxmlPath;
    private final String title;
    
    ViewType(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }
    
    public String getFxmlPath() { return fxmlPath; }
    public String getTitle() { return title; }
} 