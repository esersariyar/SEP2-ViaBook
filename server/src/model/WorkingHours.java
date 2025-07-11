package model;

import java.io.Serializable;
import java.time.LocalTime;

public class WorkingHours implements Serializable {
    private int id;
    private int dentistId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    
    public WorkingHours() {}
    
    public WorkingHours(int dentistId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dentistId = dentistId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getDentistId() {
        return dentistId;
    }
    
    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
} 