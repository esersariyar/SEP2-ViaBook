package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {
    private int id;
    private int patientId;
    private int dentistId;
    private LocalDateTime appointmentTime;
    private String status;
    private LocalDateTime createdAt;
    
    public Appointment() {}
    
    public Appointment(int patientId, int dentistId, LocalDateTime appointmentTime) {
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.appointmentTime = appointmentTime;
        this.status = "pending";
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public int getDentistId() {
        return dentistId;
    }
    
    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }
    
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", dentistId=" + dentistId +
                ", appointmentTime=" + appointmentTime +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 