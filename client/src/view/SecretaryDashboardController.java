package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import service.RMIClient;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SecretaryDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private Button createDentistButton;
    @FXML private VBox dentistForm;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveDentistButton;
    @FXML private Button cancelButton;
    @FXML private TableView<User> dentistsTable;
    
    private RMIClient rmiClient = new RMIClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadDentists();
    }
    
    private void setupTableColumns() {
        if (dentistsTable != null && dentistsTable.getColumns().size() >= 3) {
            TableColumn<User, String> nameColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(0);
            TableColumn<User, String> surnameColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(1);
            TableColumn<User, String> emailColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(2);
            
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            surnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            
            dentistsTable.setRowFactory(tv -> {
                TableRow<User> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        User selectedUser = row.getItem();
                        handleDeleteDentist(selectedUser);
                    }
                });
                return row;
            });
        }
    }
    
    private void handleDeleteDentist(User dentist) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Dentist");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + dentist.getFirstName() + " " + dentist.getLastName() + "?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (rmiClient.deleteUser(dentist.getId())) {
                showAlert("Success", "Dentist deleted successfully");
                loadDentists();
            } else {
                showAlert("Error", "Failed to delete dentist");
            }
        }
    }

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getFirstName());
            surnameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
        }
    }

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
    
    @FXML
    private void handleCreateDentist() {
        dentistForm.setVisible(true);
        dentistForm.setManaged(true);
        createDentistButton.setDisable(true);
    }
    
    @FXML
    private void handleSaveDentist() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required");
            return;
        }
        
        if (rmiClient.registerUser(password, email, firstName, lastName, "dentist")) {
            showAlert("Success", "Dentist created successfully");
            clearForm();
            hideForm();
            loadDentists();
        } else {
            showAlert("Error", "Failed to create dentist");
        }
    }
    
    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }
    
    private void loadDentists() {
        List<User> allUsers = rmiClient.getAllUsers();
        ObservableList<User> dentists = FXCollections.observableArrayList();
        
        for (User user : allUsers) {
            if ("dentist".equals(user.getRole())) {
                dentists.add(user);
            }
        }
        
        if (dentistsTable != null) {
            dentistsTable.setItems(dentists);
        }
    }
    
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
    }
    
    private void hideForm() {
        dentistForm.setVisible(false);
        dentistForm.setManaged(false);
        createDentistButton.setDisable(false);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 