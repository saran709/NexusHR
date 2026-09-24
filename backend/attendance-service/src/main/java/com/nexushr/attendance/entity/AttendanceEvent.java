package com.nexushr.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_events", indexes = {
    @Index(name = "idx_attendance_event_employee", columnList = "employee_id")
})
public class AttendanceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType; // CHECK_IN, CHECK_OUT, BREAK_START, BREAK_END

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 30)
    private String source; // BIOMETRIC, WEB, MOBILE

    @Column(length = 100)
    private String location;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public AttendanceEvent() {}

    public AttendanceEvent(UUID employeeId, String eventType, LocalDateTime timestamp, String source, String location, String deviceId) {
        this.employeeId = employeeId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.source = source;
        this.location = location;
        this.deviceId = deviceId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
