package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import viewmodel.PatientViewModel;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class PatientDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button updateProfileButton;
    
    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    @FXML private TableView pastAppointmentsTable;
    @FXML private Button cancelAppointmentButton;
    
    @FXML private ComboBox<User> dentistComboBox;
    @FXML private TextArea dentistProfileArea;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ListView<String> timeSlotsList;
    @FXML private Button bookAppointmentButton;
    
    @FXML private TableView<User> dentistsTable;
    @FXML private TextArea selectedDentistDescArea;
    
    private PatientViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new PatientViewModel();
        setupBindings();
        setupTables();
        setupEventHandlers();
    }
    
    private void setupBindings() {
        nameLabel.textProperty().bind(viewModel.nameProperty());
        surnameLabel.textProperty().bind(viewModel.surnameProperty());
        emailLabel.textProperty().bind(viewModel.emailProperty());
        
        firstNameField.textProperty().bindBidirectional(viewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(viewModel.lastNameProperty());
        emailField.textProperty().bindBidirectional(viewModel.userEmailProperty());
        
        dentistComboBox.setItems(viewModel.getDentists());
        timeSlotsList.setItems(viewModel.getAvailableTimeSlots());
        
        selectedDentistDescArea.textProperty().bind(viewModel.errorMessageProperty());
        dentistProfileArea.textProperty().bind(viewModel.dentistProfileProperty());
    }
    
    private void setupTables() {
        setupUpcomingAppointmentsTable();
        setupPastAppointmentsTable();
        setupDentistTable();
        
        upcomingAppointmentsTable.setItems(viewModel.getUpcomingAppointments());
        pastAppointmentsTable.setItems(viewModel.getPastAppointments());
        dentistsTable.setItems(viewModel.getDentists());
    }
    
    private void setupUpcomingAppointmentsTable() {
        if (upcomingAppointmentsTable != null && upcomingAppointmentsTable.getColumns().size() >= 4) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(3);
            
            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedDate(cellData.getValue())));
            
            timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedTime(cellData.getValue())));
            
            dentistColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getDentistName(cellData.getValue())));
            
            statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedStatus(cellData.getValue())));
        }
    }
    
    private void setupPastAppointmentsTable() {
        if (pastAppointmentsTable != null && pastAppointmentsTable.getColumns().size() >= 4) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> dentistColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) pastAppointmentsTable.getColumns().get(3);

            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedDate(cellData.getValue())));

            timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedTime(cellData.getValue())));

            dentistColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getDentistName(cellData.getValue())));

            statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedStatus(cellData.getValue())));
        }
    }
    
    private void setupDentistTable() {
        if (dentistsTable != null && dentistsTable.getColumns().size() >= 2) {
            TableColumn<User, String> nameColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(0);
            TableColumn<User, String> specializationColumn = (TableColumn<User, String>) dentistsTable.getColumns().get(1);
            
            nameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getDentistDisplayName(cellData.getValue())));
            
            specializationColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getDentistSpecialization(cellData.getValue())));
        }
    }
    
    private void setupEventHandlers() {
        viewModel.errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                showAlert("Info", newValue);
                viewModel.clearError();
            }
        });
        
            dentistComboBox.setCellFactory(listView -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                    setText(viewModel.getDentistDisplayName(user));
                    }
                }
            });
        
        dentistComboBox.setButtonCell(dentistComboBox.getCellFactory().call(null));
    }

    public void setUser(User user) {
        viewModel.setUser(user);
    }

    @FXML
    private void handleLogout() {
        logout(logoutButton);
    }
    
    @FXML
    private void handleUpdateProfile() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        
        viewModel.updateProfile(firstName, lastName, email);
    }
    
    @FXML
    private void handleCancelAppointment() {
        Appointment selectedAppointment = upcomingAppointmentsTable.getSelectionModel().getSelectedItem();
        viewModel.cancelAppointment(selectedAppointment);
    }
    
    @FXML
    private void handleDentistSelection() {
        User selectedDentist = dentistComboBox.getSelectionModel().getSelectedItem();
        if (selectedDentist != null) {
            viewModel.selectDentist(selectedDentist);
        }
    }
    
    @FXML
    private void handleDateSelection() {
        LocalDate selectedDate = appointmentDatePicker.getValue();
        if (selectedDate != null) {
            viewModel.selectDate(selectedDate);
        }
    }
    
    @FXML
    private void handleTimeSlotSelection(MouseEvent event) {
        String selectedItem = timeSlotsList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            viewModel.selectTimeSlot(selectedItem);
        }
    }
    
    @FXML
    private void handleBookAppointment() {
        viewModel.bookAppointment();
    }
    
    @FXML
    private void handleDentistTableSelection(MouseEvent event) {
        User selectedDentist = dentistsTable.getSelectionModel().getSelectedItem();
        if (selectedDentist != null) {
            viewModel.selectDentist(selectedDentist);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 