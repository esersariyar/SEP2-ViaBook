package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BlockedSlot implements Serializable {
    private int id;
    private int dentistId;
    private LocalDateTime blockedTime;
    private String reason;
    private LocalDateTime createdAt;
    
    public BlockedSlot() {}
    
    public BlockedSlot(int dentistId, LocalDateTime blockedTime, String reason) {
        this.dentistId = dentistId;
        this.blockedTime = blockedTime;
        this.reason = reason;
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
    
    public LocalDateTime getBlockedTime() {
        return blockedTime;
    }
    
    public void setBlockedTime(LocalDateTime blockedTime) {
        this.blockedTime = blockedTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "BlockedSlot{" +
                "id=" + id +
                ", dentistId=" + dentistId +
                ", blockedTime=" + blockedTime +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 