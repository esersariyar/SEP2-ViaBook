package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import model.DentistProfile;
import model.WorkingHours;
import model.Appointment;
import model.BlockedSlot;
import viewmodel.DentistViewModel;
import java.net.URL;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class DentistDashboardController extends BaseDashboardController implements Initializable {
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    
    @FXML private TextField specializationField;
    @FXML private TextArea descriptionArea;
    @FXML private Button updateProfileButton;
    
    @FXML private TableView<WorkingHours> workingHoursTable;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Button addWorkingHoursButton;
    @FXML private Button updateWorkingHoursButton;
    @FXML private Button deleteWorkingHoursButton;
    
    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    @FXML private Button refreshAppointmentsButton;
    
    @FXML private DatePicker slotDatePicker;
    @FXML private ListView availableSlotsList;
    @FXML private Button blockSlotButton;
    @FXML private Button unblockSlotButton;
    
    private DentistViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new DentistViewModel();
        setupBindings();
        setupTables();
        setupEventHandlers();
        
        dayComboBox.setItems(viewModel.getDayOptions());
    }
    
    private void setupBindings() {
        nameLabel.textProperty().bind(viewModel.nameProperty());
        surnameLabel.textProperty().bind(viewModel.surnameProperty());
        emailLabel.textProperty().bind(viewModel.emailProperty());
        
        specializationField.textProperty().bindBidirectional(viewModel.specializationProperty());
        descriptionArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
        
        availableSlotsList.setItems(viewModel.getAvailableSlots());
    }
    
    private void setupTables() {
        setupWorkingHoursTable();
        setupUpcomingAppointmentsTable();
        
        workingHoursTable.setItems(viewModel.getWorkingHoursList());
        upcomingAppointmentsTable.setItems(viewModel.getUpcomingAppointments());
    }
    
    private void setupWorkingHoursTable() {
        if (workingHoursTable != null && workingHoursTable.getColumns().size() >= 3) {
            TableColumn<WorkingHours, String> dayColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(0);
            TableColumn<WorkingHours, String> startColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(1);
            TableColumn<WorkingHours, String> endColumn = (TableColumn<WorkingHours, String>) workingHoursTable.getColumns().get(2);
            
            dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
            startColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedWorkingHoursStart(cellData.getValue())));
            endColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedWorkingHoursEnd(cellData.getValue())));
        }
    }
    
    private void setupUpcomingAppointmentsTable() {
        if (upcomingAppointmentsTable != null && upcomingAppointmentsTable.getColumns().size() >= 4) {
            TableColumn<Appointment, String> dateColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(0);
            TableColumn<Appointment, String> timeColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(1);
            TableColumn<Appointment, String> patientColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(2);
            TableColumn<Appointment, String> statusColumn = (TableColumn<Appointment, String>) upcomingAppointmentsTable.getColumns().get(3);
            
            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedDate(cellData.getValue())));
            
            timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedTime(cellData.getValue())));
            
            patientColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getPatientName(cellData.getValue())));
            
            statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(viewModel.getFormattedStatus(cellData.getValue())));
        }
    }
    
    private void setupEventHandlers() {
        viewModel.errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                showAlert("Info", newValue);
                viewModel.clearError();
            }
        });
        
        workingHoursTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.selectWorkingHours(newValue);
                dayComboBox.setValue(newValue.getDayOfWeek());
                startTimeField.setText(viewModel.getFormattedWorkingHoursStart(newValue));
                endTimeField.setText(viewModel.getFormattedWorkingHoursEnd(newValue));
            }
        });
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
        String specialization = specializationField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        viewModel.updateProfile(specialization, description);
    }
    
    @FXML
    private void handleAddWorkingHours() {
        String day = dayComboBox.getValue();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        
        if (viewModel.addWorkingHours(day, startTime, endTime)) {
                clearWorkingHoursFields();
        }
    }
    
    @FXML
    private void handleUpdateWorkingHours() {
        String day = dayComboBox.getValue();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        
        if (viewModel.updateWorkingHours(day, startTime, endTime)) {
                clearWorkingHoursFields();
        }
    }
    
    @FXML
    private void handleDeleteWorkingHours() {
        viewModel.deleteWorkingHours();
            clearWorkingHoursFields();
    }
    
    private void clearWorkingHoursFields() {
        dayComboBox.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        workingHoursTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleSlotDateSelection() {
        LocalDate selectedDate = slotDatePicker.getValue();
        if (selectedDate != null) {
            viewModel.selectSlotDate(selectedDate);
        }
    }
    
    @FXML
    private void handleSlotSelection(MouseEvent event) {
        String selectedItem = (String) availableSlotsList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            viewModel.selectSlot(selectedItem);
        }
    }
    
    @FXML
    private void handleBlockSlot() {
        viewModel.blockSlot();
    }

    @FXML
    private void handleUnblockSlot() {
        viewModel.unblockSlot();
    }

    @FXML
    private void handleRefreshAppointments() {
        viewModel.refreshAppointments();
    }
} 