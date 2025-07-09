package model;

import java.io.Serializable;

public class DentistProfile implements Serializable {
    private int id;
    private int userId;
    private String description;
    private String specialization;
    
    public DentistProfile() {}
    
    public DentistProfile(int userId, String description, String specialization) {
        this.userId = userId;
        this.description = description;
        this.specialization = specialization;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
} 