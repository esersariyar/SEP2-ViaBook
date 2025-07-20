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
import viewmodel.SecretaryViewModel;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

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
    
    @FXML private Button exportButton;
    
    private SecretaryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new SecretaryViewModel();
        setupBindings();
        setupTables();
        setupEventHandlers();
        viewModel.loadData();
    }
    
    private void setupBindings() {
        nameLabel.textProperty().bind(viewModel.nameProperty());
        surnameLabel.textProperty().bind(viewModel.surnameProperty());
        emailLabel.textProperty().bind(viewModel.emailProperty());
        
        statusFilterComboBox.setItems(viewModel.getStatusOptions());
        statusFilterComboBox.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());
    }
    
    private void setupTables() {
        setupPendingAppointmentsTable();
        setupAllAppointmentsTable();
        setupDentistsTable();
        
        pendingAppointmentsTable.setItems(viewModel.getPendingAppointments());
        allAppointmentsTable.setItems(viewModel.getAllAppointments());
        dentistsTable.setItems(viewModel.getDentists());
    }
    
    private void setupPendingAppointmentsTable() {
        if (pendingAppointmentsTable != null && pendingAppointmentsTable.getColumns().size() >= 5) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> patientColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(3);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) pendingAppointmentsTable.getColumns().get(4);
            
            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedDate(cellData.getValue())));
            
            timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedTime(cellData.getValue())));
            
            patientColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getPatientName(cellData.getValue())));
            
            dentistColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getDentistName(cellData.getValue())));
            
            statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedStatus(cellData.getValue())));
        }
    }
    
    private void setupAllAppointmentsTable() {
        if (allAppointmentsTable != null && allAppointmentsTable.getColumns().size() >= 5) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> patientColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(3);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) allAppointmentsTable.getColumns().get(4);
            
            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedDate(cellData.getValue())));
            
            timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedTime(cellData.getValue())));
            
            patientColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getPatientName(cellData.getValue())));
            
            dentistColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getDentistName(cellData.getValue())));
            
            statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedStatus(cellData.getValue())));
        }
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
    
    private void setupEventHandlers() {
        viewModel.errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                showAlert("Info", newValue);
                viewModel.clearError();
            }
        });
    }
    
    private void handleDeleteDentist(User dentist) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Dentist");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + dentist.getFirstName() + " " + dentist.getLastName() + "?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            viewModel.deleteDentist(dentist);
        }
    }

    public void setUser(User user) {
        viewModel.setUser(user);
    }

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
    
    @FXML
    private void handleApproveAppointment() {
        Appointment selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
        viewModel.approveAppointment(selectedAppointment);
    }
    
    @FXML
    private void handleRejectAppointment() {
        Appointment selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
        viewModel.rejectAppointment(selectedAppointment);
    }
    
    @FXML
    private void handleStatusFilter() {
        String selectedStatus = statusFilterComboBox.getSelectionModel().getSelectedItem();
        viewModel.filterAppointments(selectedStatus);
    }
    
    @FXML
    private void handleRefresh() {
        viewModel.refreshData();
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
        
        if (viewModel.createDentist(firstName, lastName, email, password)) {
            clearForm();
            hideForm();
        }
    }
    
    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
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

    @FXML
    private void handleExport() {
        LocalDate today = LocalDate.now();
        String fileName = "daily_schedule_" + today.toString().replace("-", "") + ".csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Date,Time,Patient,Dentist,Status\n");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            for (Appointment appointment : viewModel.getAllAppointments()) {
                if (appointment.getAppointmentTime().toLocalDate().equals(today)) {
                    String line = String.format("%s,%s,%s,%s,%s\n",
                        appointment.getAppointmentTime().format(dateFormatter),
                        appointment.getAppointmentTime().format(timeFormatter),
                        viewModel.getPatientName(appointment),
                        viewModel.getDentistName(appointment),
                        appointment.getStatus()
                    );
                    writer.write(line);
                }
            }
            showAlert("Success", "Daily schedule exported to " + fileName);
        } catch (IOException e) {
            showAlert("Error", "Failed to export schedule: " + e.getMessage());
        }
    }
} 