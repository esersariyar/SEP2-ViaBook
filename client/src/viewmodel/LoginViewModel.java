import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.User;
import model.LoginModel;
import service.RMIClient;

public class LoginViewModel {
    private LoginModel model;
    private StringProperty email;
    private StringProperty password;
    private StringProperty errorMessage;
    private RMIClient rmiClient;
    private User authenticatedUser;
    
    public LoginViewModel() {
        this.model = new LoginModel();
        this.email = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.errorMessage = new SimpleStringProperty("");
        this.rmiClient = new RMIClient();
        this.authenticatedUser = null;
    }
    
    public StringProperty emailProperty() {
        return email;
    }
    
    public StringProperty passwordProperty() {
        return password;
    }
    
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
    
    public boolean handleLogin() {
        String mail = email.get();
        String pass = password.get();
        
        model.setEmail(mail);
        model.setPassword(pass);
        
        if (!model.isValid()) {
            errorMessage.set("Email and password cannot be empty");
            return false;
        }
        errorMessage.set("");
        if (!rmiClient.isServerConnected()) {
            errorMessage.set("Cannot connect to ViaBook server");
            rmiClient.reconnect();
            if (!rmiClient.isServerConnected()) {
                errorMessage.set("Server connection failed. Please check if server is running.");
                return false;
            }
        }
        try {
            authenticatedUser = rmiClient.authenticateUser(mail, pass);
            if (authenticatedUser != null) {
                System.out.println("ViaBook Client: Login successful for user: " + authenticatedUser.getEmail());
                System.out.println("ViaBook Client: User role: " + authenticatedUser.getRole());
                return true;
            } else {
                errorMessage.set("Invalid email or password");
                return false;
            }
        } catch (Exception e) {
            errorMessage.set("Login failed: " + e.getMessage());
            System.err.println("ViaBook Client: Login error: " + e.getMessage());
            return false;
        }
    }
    
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
    
    public void clearFields() {
        email.set("");
        password.set("");
        errorMessage.set("");
    }
} 