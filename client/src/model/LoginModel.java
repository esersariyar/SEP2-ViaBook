package model;

public class LoginModel {
    private String email;
    private String password;
    
    public LoginModel() {
        this.email = "";
        this.password = "";
    }
    
    public LoginModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isValid() {
        return email != null && !email.trim().isEmpty()
            && password != null && !password.trim().isEmpty();
    }
} 