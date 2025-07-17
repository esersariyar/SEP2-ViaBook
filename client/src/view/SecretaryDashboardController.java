package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.User;
import model.Appointment;
import service.RMIClient;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class SecretaryDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TableView<Appointment> pendingAppointmentsTable;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private Button refreshButton;
    @FXML private TableView<Appointment> allAppointmentsTable;
    
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
    private ObservableList<Appointment> pendingAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private List<User> cachedUsers = null;
    private java.util.Map<Integer, User> userMap = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPendingAppointmentsTable();
        setupAllAppointmentsTable();
        setupDentistsTable();
        setupStatusFilter();
        loadDentists();
        loadAllAppointments();
    }
    
    private void setupPendingAppointmentsTable() {
        if (pendingAppointmentsTable != null && pendingAppointmentsTable.getColumns().size() >= 5) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> patientColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(3);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(4);
            
            dateColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(
                    cellData.getValue().getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            });
            
            timeColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(
                    cellData.getValue().getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            });
            
            patientColumn.setCellValueFactory(cellData -> {
                User patient = getUserById(cellData.getValue().getPatientId());
                return new SimpleStringProperty(
                    patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown"
                );
            });
            
            dentistColumn.setCellValueFactory(cellData -> {
                User dentist = getUserById(cellData.getValue().getDentistId());
                return new SimpleStringProperty(
                    dentist != null ? "Dr. " + dentist.getFirstName() + " " + dentist.getLastName() : "Unknown"
                );
            });
            
            statusColumn.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatus();
                return new SimpleStringProperty(
                    status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown"
                );
            });
            
            pendingAppointmentsTable.setItems(pendingAppointments);
        }
    }
    
    private void setupAllAppointmentsTable() {
        if (allAppointmentsTable != null && allAppointmentsTable.getColumns().size() >= 5) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> patientColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(3);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(4);
            
            dateColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(
                    cellData.getValue().getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            });
            
            timeColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(
                    cellData.getValue().getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            });
            
            patientColumn.setCellValueFactory(cellData -> {
                User patient = getUserById(cellData.getValue().getPatientId());
                return new SimpleStringProperty(
                    patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown"
                );
            });
            
            dentistColumn.setCellValueFactory(cellData -> {
                User dentist = getUserById(cellData.getValue().getDentistId());
                return new SimpleStringProperty(
                    dentist != null ? "Dr. " + dentist.getFirstName() + " " + dentist.getLastName() : "Unknown"
                );
            });
            
            statusColumn.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatus();
                return new SimpleStringProperty(
                    status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Unknown"
                );
            });
            
            allAppointmentsTable.setItems(allAppointments);
        }
    }
    
    private void setupStatusFilter() {
        if (statusFilterComboBox != null) {
            statusFilterComboBox.getItems().addAll("All", "Pending", "Approved", "Cancelled");
            statusFilterComboBox.getSelectionModel().selectFirst();
        }
    }
    
    private void cacheUsers() {
        cachedUsers = rmiClient.getAllUsers();
        userMap = new java.util.HashMap<>();
        for (User user : cachedUsers) {
            userMap.put(user.getId(), user);
        }
    }

    private User getUserById(int userId) {
        if (userMap == null) {
            cacheUsers();
        }
        return userMap.get(userId);
    }
    
    private void setupDentistsTable() {
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
    
    private void loadPendingAppointments() {
        pendingAppointments.clear();
        for (Appointment appointment : allAppointments) {
            if ("pending".equals(appointment.getStatus())) {
                pendingAppointments.add(appointment);
            }
        }
    }
    
    private void loadAllAppointments() {
        // Get all appointments from all dentists
        if (userMap == null) {
            cacheUsers();
        }
        allAppointments.clear();
        for (User user : cachedUsers) {
            if ("dentist".equals(user.getRole())) {
                List<Appointment> dentistAppointments = rmiClient.getDentistAppointments(user.getId());
                allAppointments.addAll(dentistAppointments);
            }
        }
        loadPendingAppointments();
    }
    
    private void handleDeleteDentist(User dentist) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Dentist");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + dentist.getFirstName() + " " + dentist.getLastName() + "?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (rmiClient.deleteUser(dentist.getId())) {
                showAlert("Success", "Dentist deleted successfully");
                userMap = null; // cache'i sıfırla
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
    private void handleApproveAppointment() {
        Appointment selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("Error", "Please select an appointment to approve");
            return;
        }
        
        if (rmiClient.updateAppointmentStatus(selectedAppointment.getId(), "approved")) {
            showAlert("Success", "Appointment approved successfully");
            loadAllAppointments();
        } else {
            showAlert("Error", "Failed to approve appointment");
        }
    }
    
    @FXML
    private void handleRejectAppointment() {
        Appointment selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("Error", "Please select an appointment to reject");
            return;
        }
        
        if (rmiClient.updateAppointmentStatus(selectedAppointment.getId(), "cancelled")) {
            showAlert("Success", "Appointment rejected successfully");
            loadAllAppointments();
        } else {
            showAlert("Error", "Failed to reject appointment");
        }
    }
    
    @FXML
    private void handleStatusFilter() {
        String selectedStatus = statusFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedStatus != null && !"All".equals(selectedStatus)) {
            String status = selectedStatus.toLowerCase();
            ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
            
            for (Appointment appointment : allAppointments) {
                if (status.equals(appointment.getStatus())) {
                    filteredAppointments.add(appointment);
                }
            }
            
            allAppointmentsTable.setItems(filteredAppointments);
        } else {
            allAppointmentsTable.setItems(allAppointments);
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadAllAppointments();
        statusFilterComboBox.getSelectionModel().selectFirst();
        showAlert("Info", "Appointments refreshed");
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
            userMap = null; // cache'i sıfırla
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
        if (userMap == null) {
            cacheUsers();
        }
        ObservableList<User> dentists = FXCollections.observableArrayList();
        
        for (User user : cachedUsers) {
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