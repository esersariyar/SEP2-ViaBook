import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
    private LoginModel model;
    private StringProperty username;
    private StringProperty password;
    private StringProperty errorMessage;
    
    public LoginViewModel() {
        this.model = new LoginModel();
        this.username = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.errorMessage = new SimpleStringProperty("");
    }
    
    public StringProperty usernameProperty() {
        return username;
    }
    
    public StringProperty passwordProperty() {
        return password;
    }
    
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
    
    public boolean handleLogin() {
        String user = username.get();
        String pass = password.get();
        
        model.setUsername(user);
        model.setPassword(pass);
        
        if (!model.isValid()) {
            errorMessage.set("Username and password cannot be empty");
            return false;
        }
        
        errorMessage.set("");
        
        System.out.println("Login attempt:");
        System.out.println("Username: " + user);
        System.out.println("Password: " + "*".repeat(pass.length()));
        
        return true;
    }
    
    public void clearFields() {
        username.set("");
        password.set("");
        errorMessage.set("");
    }
} 